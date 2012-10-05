/*
  Simulator

  Takes one argument: the number of ticks to perform.

  Example usage: java Simulator 5000
*/

class Simulator {

    private int numPacketsInQueue;
    private int tDeparture;
    private int tArrival;

    /* Initialise important terms such as t_arrival = exponential r.v, # of pkts in queue = 0,
       t_departure = t_arrival ( this implies that first time departure will be called as soon as a 
       packet arrives in the queue */
    public void Simulator(int ticks) {
        numPacketsInQueue = 0;
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
    }

    /* Check the queue for the packet, if head of the queue is empty, return 0 else if the 
       queue is non-empty delete the packet from the queue after an elapse of the 
       deterministic service time. */
    private void departure(int t) {
    }

    /* Calculate and display the results such as average number of packets in queue, 
       average delay in queue and idle time for the server. */
    private void computePerformances() {
    }

    public static void main(String[] args) {
        Simulator s = new Simulator();
        s.startSimulation(Integer.parseInt(args[0]));
    }
}