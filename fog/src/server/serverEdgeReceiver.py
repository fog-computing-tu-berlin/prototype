import asyncio

import zmq
import zmq.asyncio

from core.config import Config
from core.messageProcessor import MessageProcessor


class ServerEdgeReceiver:

    context = zmq.asyncio.Context()

    def __init__(self, config: Config, message_processor: MessageProcessor) -> None:
        super().__init__()
        self.__config = config
        self.__message_processor = message_processor

    async def recv_and_process(self) -> None:
        # noinspection PyUnresolvedReferences
        socket = self.context.socket(zmq.REP)
        socket.bind('tcp://*:' + str(self.__config.get_edge_receiver_listen_port()))

        while True:
            try:
                message = await socket.recv()
                message = str(message, 'UTF-8')

                if self.__config.is_debug_logging():
                    print("Received request: ", message)

                reply = await self.__message_processor.process_message(message)
                # noinspection PyUnresolvedReferences
                await socket.send(bytes(reply, 'UTF-8'), zmq.NOBLOCK)
            except KeyboardInterrupt:
                break
            except zmq.ZMQError:
                await asyncio.sleep(60)


# Example for Multithreading this:
# Alternative Multithreading maybe with: http://zguide.zeromq.org/py:ppqueue
#def worker_routine(worker_url, context=None):
#    """Worker routine"""
#    context = context or zmq.Context.instance()
#    # Socket to talk to dispatcher
#    socket = context.socket(zmq.REP)
#
#    socket.connect(worker_url)
#
#    while True:
#        string  = socket.recv()
#
#        print("Received request: [ %s ]" % (string))
#
#        # do some 'work'
#        time.sleep(1)
#
#        #send reply back to client
#        socket.send(b"World")
#
#def main():
#    """Server routine"""
#
#    url_worker = "inproc://workers"
#    url_client = "tcp://*:5555"
#
#    # Prepare our context and sockets
#    context = zmq.Context.instance()
#
#    # Socket to talk to clients
#    clients = context.socket(zmq.ROUTER)
#    clients.bind(url_client)
#
#    # Socket to talk to workers
#    workers = context.socket(zmq.DEALER)
#    workers.bind(url_worker)
#
#    # Launch pool of worker threads
#    for i in range(5):
#        thread = threading.Thread(target=worker_routine, args=(url_worker,))
#        thread.start()
#
#    zmq.proxy(clients, workers)
#
#    # We never get here but clean up anyhow
#    clients.close()
#    workers.close()
#    context.term()
#
#if __name__ == "__main__":
#    main()
