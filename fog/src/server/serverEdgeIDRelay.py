import zmq
import zmq.asyncio

from core.config import Config


class ServerIDReceiver:
    context = zmq.asyncio.Context()

    def __init__(self, config: Config) -> None:
        super().__init__()
        self.__config = config

    # noinspection PyUnresolvedReferences
    async def recv_and_process(self):
        socket_req = self.__setup_socket_req()
        socket_rep = self.__setup_socket_rep()

        while True:
            message = await socket_rep.recv()

            if self.__config.is_debug_logging():
                print("EdgeIDRelay received request: " + str(message, 'UTF-8'))

            await socket_req.send(message)
            try:
                edge_id = await socket_req.recv()
                await socket_rep.send(edge_id)
            except zmq.error.Again as e:
                # TODO Issue
                # The following scenario causes a trackback:
                # Traceback (most recent call last):
                #   File "C:\repos\Fog-Computing\fog\src\server\serverEdgeIDRelay.py", line 27, in recv_and_process
                #     edge_id = await socket_req.recv()
                #   File "C:\repos\Fog-Computing\fog\venv\lib\site-packages\zmq\_future.py", line 433, in _handle_recv
                #     result = recv(**kwargs)
                #   File "zmq\backend\cython\socket.pyx", line 788, in zmq.backend.cython.socket.Socket.recv
                #   File "zmq\backend\cython\socket.pyx", line 824, in zmq.backend.cython.socket.Socket.recv
                #   File "zmq\backend\cython\socket.pyx", line 191, in zmq.backend.cython.socket._recv_copy
                #   File "zmq\backend\cython\socket.pyx", line 186, in zmq.backend.cython.socket._recv_copy
                #   File "zmq\backend\cython\checkrc.pxd", line 19, in zmq.backend.cython.checkrc._check_rc
                ## This happens when we request ids -> timeout -> request new ids -> get the answer for the first request --> above stacktrace
                await socket_rep.send_string('Upstream timout')

    # noinspection PyUnresolvedReferences
    def __setup_socket_rep(self) -> zmq.asyncio.Socket:
        socket_rep = self.context.socket(zmq.REP)
        socket_rep.setsockopt(zmq.SNDHWM, self.__config.get_edge_id_relay_max_queue_length())
        socket_rep.setsockopt(zmq.RCVHWM, self.__config.get_edge_id_relay_max_queue_length())
        socket_rep.bind('tcp://*:' + str(self.__config.get_edge_id_generator_listen_port()))

        return socket_rep

    # noinspection PyUnresolvedReferences
    def __setup_socket_req(self) -> zmq.asyncio.Socket:
        socket_req = self.context.socket(zmq.REQ)
        socket_req.connect(self.__config.get_edge_id_upstream_url())
        socket_req.setsockopt(zmq.RCVTIMEO, self.__config.get_cloud_id_relay_cloud_timeout())
        socket_req.setsockopt(zmq.SNDTIMEO, self.__config.get_cloud_id_relay_cloud_timeout())
        socket_req.setsockopt(zmq.REQ_CORRELATE, 1)
        socket_req.setsockopt(zmq.REQ_RELAXED, 1)
        socket_req.setsockopt(zmq.LINGER, 0)
        socket_req.setsockopt(zmq.SNDHWM, self.__config.get_edge_id_relay_max_queue_length())
        socket_req.setsockopt(zmq.RCVHWM, self.__config.get_edge_id_relay_max_queue_length())

        return socket_req
