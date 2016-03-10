import multiprocessing
from multiprocessing import Process
from collections import deque
import random
import sys
from datetime import datetime
import time
import Queue


def f(own, first, second, outfile):
    rate = random.randint(1, 6)
    # how long each tick should be
    tick_interval = 1 / float(rate)
    # local queue
    q = deque()
    # the all important logical clock!
    logical_clock = 0

    ownname, ownq = own
    firstname, tofirst = first
    secondname, tosecond = second
    with open(outfile, 'w') as f:
        # loop
        while(True):
            # block for tick interval amount of time overall
            time_left = tick_interval
            while(time_left > 0):
                try:
                    start = time.time()
                    new_elt = ownq.get(True, time_left)
                    q.append(new_elt)
                    end = time.time()
                    time_left -= (end - start)
                except Queue.Empty:
                    break
            to_print = ""
            if(len(q) == 0):
                randgen = random.randint(1, 10)
                logical_clock += 1
                if randgen == 1:
                    tofirst.put((ownname, logical_clock))
                    to_print += ("Sent message to: " + firstname)                    
                elif randgen == 2:
                    tosecond.put((ownname, logical_clock))
                    to_print += ("Sent message to: " + secondname)
                elif randgen == 3:
                    tofirst.put((ownname, logical_clock))
                    tosecond.put((ownname, logical_clock))
                    to_print += "Send message to: both"
                else:
                    to_print += "Internal Event"
            else:
                pname, message = q.popleft()
                logical_clock = max(message, logical_clock) + 1
                to_print += "Received message: " + str(message) + " from: " + str(pname) \
                            + "with remaining queue size: " + str(len(q))
            to_print += " at logical time: " + str(logical_clock) + " and system time " \
                        + str(datetime.now())
            print(to_print)

def main():
    first = ("p1", multiprocessing.Queue())
    second = ("p2", multiprocessing.Queue())
    third = ("p3", multiprocessing.Queue())
    
    p1 = Process(target = f, args = (first, second, third, "p1.txt"))
    p2 = Process(target = f, args = (second, third, first, "p2.txt"))
    p3 = Process(target = f, args = (third, first, second, "p3.txt"))
    p1.start()
    p2.start()
    p3.start()

main()
