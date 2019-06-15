import asyncio

import zmq
import zmq.asyncio


class MessageCache:
    _context: zmq.asyncio.Context = zmq.asyncio.Context()

    def __init__(self, max_queue_length: int = 100000, unique_name: str = None) -> None:
        super().__init__()

        if unique_name is None:
            unique_name = random_string()
        url = 'inproc://' + unique_name
        self.__pub_socket = self.__setup_pub_socket(url, max_queue_length)
        self.__sub_socket = self.__setup_sub_socket(url, max_queue_length)

    # noinspection PyUnresolvedReferences
    def __setup_pub_socket(self, url: str, max_queue_length: int) -> zmq.asyncio.Socket:
        socket = self._context.socket(zmq.PUB)
        socket.setsockopt(zmq.SNDHWM, max_queue_length)
        socket.setsockopt(zmq.RCVHWM, max_queue_length)
        socket.bind(url)

        return socket

    # noinspection PyUnresolvedReferences
    def __setup_sub_socket(self, url: str, max_queue_length: int) -> zmq.asyncio.Socket:
        socket = self._context.socket(zmq.SUB)
        socket.connect(url)
        socket.setsockopt(zmq.SNDHWM, max_queue_length)
        socket.setsockopt(zmq.RCVHWM, max_queue_length)
        socket.setsockopt_string(zmq.SUBSCRIBE, '')

        return socket

    async def __add_to_cache(self, message: bytes):
        await self.__pub_socket.send(message)

    async def publish(self, message: bytes) -> None:
        await self.__add_to_cache(message)

    def terminate(self) -> None:
        self._context.destroy()

    async def process_message(self, message: bytes) -> None:
        print(message.decode("utf-8"))

    async def process_loop(self) -> None:
        while True:
            try:
                message = await self.__sub_socket.recv()
                await self.process_message(message)
            except KeyboardInterrupt:
                break


def random_string(string_length=10):
    """Generate a random string of fixed length """
    import string
    import random
    letters = string.ascii_lowercase
    return ''.join(random.choice(letters) for i in range(string_length))


if __name__ == "__main__":
    message_cache = MessageCache()

    loop = asyncio.get_event_loop()
    loop.run_until_complete(message_cache.publish(b"Test Message 1"))
    loop.run_until_complete(message_cache.publish(b"Test Message 2"))
    loop.create_task(message_cache.process_loop())
    loop.run_forever()

    # Never going to be reached
    loop.close()
    message_cache.terminate()
