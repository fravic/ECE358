main() {
  /*Initialise important terms such as t_arrival = exponential r.v, # of pkts in queue = 0, 
    t_departure = t_arrival ( this implies that first time departure will be called as soon as a 
    packet arrives in the queue*/
  Start_simulation (ticks);
  Compute_performances ( );
}

Start_simulation(int ticks) {
  for (t=1; t<= Ticks; t++) {
      Arrival(t);
      Departure(t);
    }
}

Arrival(int t) {
  /* Generate a packet as per the exponential distribution and insert the packet in the 
     queue (an array or a linked list)*/
}

Departure(int t) {
  /* Check the queue for the packet, if head of the queue is empty, return 0 else if the 
     queue is non-empty delete the packet from the queue after an elapse of the 
     deterministic service time. */
}

Compute_performances() {
  /*Calculate and display the results such as average number of packets in queue, 
    average delay in queue and idle time for the server. */
}
