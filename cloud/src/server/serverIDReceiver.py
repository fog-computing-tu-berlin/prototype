import zmq
import zmq.asyncio
from handler.edgeIDGenerator import EdgeIDGenerator


class ServerIDReceiver:
    context = zmq.asyncio.Context()

    def __init__(self, edge_id_generator: EdgeIDGenerator) -> None:
        super().__init__()
        self.edge_id_generator = edge_id_generator

    async def recv_and_process(self):
        socket = self.context.socket(zmq.REP)
        socket.bind("tcp://*:5559")
        while True:
            #  Wait for next request from client
            message = await socket.recv()
            message = str(message)

            print("Received request: ", message)

            reply = await self.edge_id_generator.create_new_id(message)
            await socket.send(bytes(reply, 'UTF-8'))
