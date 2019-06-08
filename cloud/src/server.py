import serverZMQ
import serverFlask
import asyncio


async def main():
    zmq = serverZMQ.recv_and_process()
    # Flask currently blocks zmq
    #serverFlask.startFlask()
    await zmq


if __name__ == "__main__":
    loop = asyncio.get_event_loop()
    loop.run_until_complete(main())
    loop.close()


