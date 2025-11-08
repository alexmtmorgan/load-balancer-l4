# Description
This is a simple Java implementation of an L4 Load balancer.

The load balancer:
1. Can accept multiple simultaneous client connections
2. Supports simultaneous bi-directional data flow between client and server
3. Uses a simple round-robin load balancing strategy
4. Contains a scheduled server health-check for:
   1. pruning unhealthy services 
   2. re-instating previously pruned (unhealthy) services

# Running the app

1. from the root directory, run

`mvn package`
2. then run 

`java -jar target/load-balancer-l4-1.0-SNAPSHOT.jar`