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
    private int _totalSojurnTime;
    private int _totalBusyTime;
    

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
        _totalBusyTime = 0;
        _totalSojurnTime = 0;

        for (int t = 1; t <= ticks * MICROSECONDS; t++) {
            arrival(t);
            departure(t);

            _totalPacketsInQueue += _queue.size();
        }
        computePerformance(ticks * MICROSECONDS);
    }

    /* Generate a packet as per the exponential distribution and insert the packet in the 
       queue (an array or a linked list) */
    private void arrival(int t) {
        if (shouldGeneratePacket(t)) {
            _queue.add(new Integer(t));
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

        // Otherwise, if we're not servicing yet, start servicing
        if (_serviceStartTime < 0) {
            _serviceStartTime = t;
        } else if (t >= _serviceStartTime + _serviceTime) {
            // Done servicing, remove from queue
            int packetStartTime = ((Integer)_queue.remove()).intValue();
            _totalSojurnTime += t - packetStartTime; 
            _numPacketsProcessed++;
            _totalBusyTime++;

            // Stop servicing
            _serviceStartTime = -1;
        } else {
            // Still servicing
            _totalBusyTime++;
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
        System.out.println("Average sojurn time: " + (_totalSojurnTime / (double)_numPacketsProcessed));

        // Percentage idle time
        System.out.println("Percentage idle time: " + ((_totalBusyTime / (double)ticks) * 100) + "%");

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