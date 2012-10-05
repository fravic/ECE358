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
import java.util.Random;

class Simulator {

    int MICROSECONDS = 1000000;

    // Parameters
    private int _packetsPerSecond;
    private int _packetLength;
    private int _transmissionRate;

    // Utility vars
    private Queue _queue;
    private int _nextArrivalTime;
    private int _serviceStartTime;
    private int _serviceTime;
    
    // Performance metrics
    private int _totalPacketsInQueue;
    private int _numPacketsProcessed;
    private int _totalPacketProcessTime;
    

    /* Initialise important terms such as t_arrival = exponential r.v, # of pkts in queue = 0,
       t_departure = t_arrival ( this implies that first time departure will be called as soon as a 
       packet arrives in the queue */
    public Simulator(int packetsPerSecond, int packetLength, int transmissionRate) {
        _packetsPerSecond = packetsPerSecond;
        _packetLength = packetLength;
        _transmissionRate = transmissionRate;

        _queue = new LinkedList<Integer>();
        _nextArrivalTime = 0;
        _serviceStartTime = -1; // Begin not servicing
        _serviceTime = (int)(((double)_packetLength / (double)_transmissionRate) * MICROSECONDS);
        System.out.println("Service time " + _serviceTime);
    }

    public void startSimulation(int ticks) {
        _totalPacketsInQueue = 0;
        _numPacketsProcessed = 0;
        _totalPacketProcessTime = 0;

        for (int t = 1; t <= ticks * MICROSECONDS; t++) {
            arrival(t);
            departure(t);

            _totalPacketsInQueue += _queue.size();
        }
        computePerformance(ticks);
    }

    public void testRandomNumberGenerator() {
        // Ensure proper random number generation
        double total = 0;

        System.out.println("Testing random number generator...");
        for (int i = 0; i < 1000; i++) {
            total += generateArrivalTime();
        }
        System.out.println("Mean: (expected 0.01):" + (total/1000));
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
        // If the queue is empty, we have some idle time
        if (_queue.peek() == null) {
            return;
        }

        // The service time is lambda * L / C

        
        // Otherwise, if we're not servicing yet, start servicing
        if (_serviceStartTime < 0) {
            _serviceStartTime = t;
        } else if (t >= _serviceStartTime + _serviceTime) {
            // Done servicing, remove from queue
            _queue.remove();
            _numPacketsProcessed++;
        } else {
            // Still servicing
            _totalPacketProcessTime++;
        }
    }

    private boolean shouldGeneratePacket(int t) {
        if (t >= _nextArrivalTime) {
            _nextArrivalTime += (int)(generateArrivalTime() * MICROSECONDS);
            return true;
        }
        return false;
    }

    private double generateArrivalTime() {
        Random generator = new Random();
        double uniform = generator.nextDouble();
        double arrivalTime = Math.log(1 - uniform) / (-_packetsPerSecond);
        return arrivalTime;
    }

    private void computePerformance(int ticks) {
        // Average number of packets in queue
        System.out.println("Avergae number of packets in queue: " + (_totalPacketsInQueue / (double)ticks));
        
        // Average time spent in queue per packet
        System.out.println("Average time spent in queue: " + (_numPacketsProcessed / (double)_totalPacketProcessTime));

        // Percentage idle time
        System.out.println("Percentage idle time: " + (_totalPacketProcessTime / (double)ticks * 100) + "%");

        // Packet loss probability
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