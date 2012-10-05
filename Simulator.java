/*
  Simulator.java

  Arguments
  - n: Number of ticks to perform
  - lambda: Average number of packets generated/arrived per second
  - L: Length of each packet
  - C: Transmission rate of the output link in bits per second

  Example usage: java Simulator 5000 5 10 50
*/

import java.util.Queue;
import java.util.LinkedList;

class Simulator {

    private int _packetsPerSecond;
    private int _packetLength;
    private int _transmissionRate;

    private Queue _queue;
    private int _nextArrivalTime;

    /* Initialise important terms such as t_arrival = exponential r.v, # of pkts in queue = 0,
       t_departure = t_arrival ( this implies that first time departure will be called as soon as a 
       packet arrives in the queue */
    public Simulator(int packetsPerSecond, int packetLength, int transmissionRate) {
        _packetsPerSecond = packetsPerSecond;
        _packetLength = packetLength;
        _transmissionRate = transmissionRate;

        _queue = new LinkedList<Integer>();
        _nextArrivalTime = 0;
    }

    public void startSimulation(int ticks) {
        for (int t = 1; t <= ticks; t++) {
            arrival(t);
            departure(t);
        }
    }

    /* Generate a packet as per the exponential distribution and insert the packet in the 
       queue (an array or a linked list) */
    private void arrival(int t) {
        if (shouldGeneratePacket(t)) {
            _queue.add(new Integer(1));
        }
    }

    /* Check the queue for the packet, if head of the queue is empty, return 0 else if the 
       queue is non-empty delete the packet from the queue after an elapse of the 
       deterministic service time. */
    private void departure(int t) {
    }

    private boolean shouldGeneratePacket(int t) {
        if (t >= _nextArrivalTime) {
            _nextArrivalTime += generateArrivalTime();
            return true;
        }
        return false;
    }

    private int generateArrivalTime() {
        return 1000 / _packetsPerSecond;
    }

    /* Calculate and display the results such as average number of packets in queue, 
       average delay in queue and idle time for the server. */
    private void computePerformances() {
    }

    public static void main(String[] argv) {
        if (argv.length < 4) {
            System.out.println("Usage: java Simulator n lambda L C");
            System.exit(0);
        }

        Simulator s = new Simulator(
                                    Integer.parseInt(argv[1]),
                                    Integer.parseInt(argv[2]),
                                    Integer.parseInt(argv[3])
                                    );
        s.startSimulation(Integer.parseInt(argv[0]));
        
        System.exit(0);
    }
}