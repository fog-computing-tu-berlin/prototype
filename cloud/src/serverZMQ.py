import asyncio
import messageProcessor as mp
import zmq
import zmq.asyncio

context = zmq.asyncio.Context()

async def recv_and_process():
    socket = context.socket(zmq.REP)
    socket.bind("tcp://*:5555")
    messageProcessor = mp.MessageProcessor()
    while True:
        #  Wait for next request from client
        message = await socket.recv_string()
        print("Received request: ", message)
        reply = await messageProcessor.process_message(message)
        await socket.send_json(reply)

if __name__ == "__main__":
    loop = asyncio.get_event_loop()
    loop.run_until_complete(recv_and_process())
    loop.close()


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