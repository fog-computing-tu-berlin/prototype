from core.messageProcessor import MessageProcessor
import zmq
import zmq.asyncio
import time

context = zmq.asyncio.Context()


async def recv_and_process(message_processor: MessageProcessor) -> None:
    socket = context.socket(zmq.REP)
    socket.bind("tcp://*:5555")

    while True:
        try:
            message = await socket.recv_string()
            print("Received request: ", message)
            reply = await message_processor.process_message(message)
            await socket.send(bytes(reply, 'UTF-8'), zmq.NOBLOCK)
        except KeyboardInterrupt:
            break
        except zmq.ZMQError:
            time.sleep(60)


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
