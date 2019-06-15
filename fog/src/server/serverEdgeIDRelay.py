from datetime import datetime, timedelta

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
        last_timeout = datetime.now()

        while True:
            message = await socket_rep.recv()

            if self.__config.is_debug_logging():
                print("EdgeIDRelay received request: " + str(message, 'UTF-8'))

            await socket_req.send(message)
            try:
                edge_id = await socket_req.recv()
                await socket_rep.send(edge_id)
            except zmq.error.Again:
                # Workaround for broken sockets; This causes message drops, but heals the socket that would causes faulty messages anyway
                ## This happens when we request ids -> timeout -> request new ids -> get the answer for the first request --> Some breaks further requests
                ## Probably message trackng is broken. Just kill socket
                if last_timeout + timedelta(milliseconds=self.__config.get_cloud_id_relay_cloud_timeout()) > datetime.now():
                    await socket_rep.send_string('Upstream connection error')
                    print('Upstream ID Socket broken. Recreating...')
                    socket_req.close(linger=500)
                    socket_req = self.__setup_socket_req()
                    print('Recreate successful')
                else:
                    await socket_rep.send_string('Upstream timout')

                last_timeout = datetime.now()

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
