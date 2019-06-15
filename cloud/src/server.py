import serverZMQ
import asyncio


async def main():
    await serverZMQ.recv_and_process()


if __name__ == "__main__":
    loop = asyncio.get_event_loop()
    loop.run_until_complete(main())
    loop.close()


