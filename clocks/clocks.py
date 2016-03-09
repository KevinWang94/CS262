from multiprocessing import Process, Pipe
from collections import deque
import random
import sys
import time


def f(tofirst, tosecond, outfile):
    rate = random.randint(1, 6)
    sleep_interval = 1 / float(rate)
    print(sleep_interval)
    sys.stdout.flush()
    q = deque()
    logical_clock = 0
    with open(outfile, 'w') as f:
        while(True):
            start_time = time.time()
            while(tofirst.poll()):
                q.append(tofirst.recv())
            while(tosecond.poll()):
                q.append(tosecond.recv())
            if(len(q) == 0):
                randgen = random.randint(1, 10)
                if randgen == 1:
                    tofirst.send(logical_clock)
                elif randgen == 2:
                    tosecond.send(logical_clock)
                elif randgen == 3:
                    tofirst.send(logical_clock)
                    tosecond.send(logical_clock)
                else:
                    logical_clock += 1
            else:
                message = q.popleft()
                logical_clock = max(message, logical_clock + 1)
                f.write(str(message) + str(time.time()) + str(len(q)) + str(logical_clock))

def main():
    pipe12, pipe21 = Pipe()
    pipe13, pipe31 = Pipe()
    pipe23, pipe32 = Pipe()
    p1 = Process(target = f, args = (pipe12, pipe13, "p1.txt"))
    p2 = Process(target = f, args = (pipe23, pipe21, "p2.txt"))
    p3 = Process(target = f, args = (pipe31, pipe32, "p3.txt"))
    p1.start()
    p2.start()
    p3.start()

main()