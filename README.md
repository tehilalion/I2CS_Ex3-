# I2CS_Ex3
Pac-Man AI: Priority-Based Decision Algorithm
This algorithm implements a smart, reactive agent for the Pac-Man game. Instead of moving randomly, it analyzes the game board in real-time to balance survival (escaping ghosts), aggression (chasing edible ghosts), and efficiency (collecting food/power-ups).

What Pacman prioritizes:

The algorithm follows a strict "Priority Queue" of behaviors. In every frame, it checks conditions in this specific order:

Emergency Survival: If a dangerous ghost is within 7 tiles, Pac-Man looks for an immediate Power-Up or executes an escape maneuver.

Hunting Edible Ghosts: If ghosts are in "edible" mode (scared), Pac-Man ignores food and chases the nearest ghost to maximize points.

Strategic Power-Ups: If a Power-Up is nearby and a ghost is approaching (within 15 tiles), Pac-Man moves to grab the Power-Up to turn the tables.

Efficiency (Food Gathering): If no immediate threats or hunting opportunities exist, Pac-Man finds the shortest path to the nearest piece of food.

Random Walk: If no food or targets are reachable, it moves randomly to explore the map.
