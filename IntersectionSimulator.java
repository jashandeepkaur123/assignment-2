package jashasndeep_assg_2;

import java.util.*;

class Car {
    private int speed;        // Speed in km/h
    private String direction; // Direction: "N" (North), "S" (South), "E" (East), "W" (West)
    private String color;     // Color of the car

    public Car(int speed, String direction, String color) {
        this.speed = speed;
        this.direction = direction;
        this.color = color;
    }

    public int getSpeed() {
        return speed;
    }

    public void accelerate(int amount) {
        speed += amount;
    }

    public void brake(int amount) {
        speed = Math.max(0, speed - amount);
    }

    public String getDirection() {
        return direction;
    }

    public String getColor() {
        return color;
    }
}

class TrafficLight {
    private boolean isGreen;

    public TrafficLight() {
        isGreen = false; // Initialize traffic light to red
    }

    public boolean isGreen() {
        return isGreen;
    }

    public void setGreen(boolean isGreen) {
        this.isGreen = isGreen;
    }
}

public class IntersectionSimulator {
    private static final int SIMULATION_TIME_SECONDS = 10;

    public static void main(String[] args) {
        TrafficLight trafficLight = new TrafficLight();
        Random rand = new Random();

        // Create cars with random speeds, directions, and colors
        Car[] cars = new Car[10];
        String[] directions = {"N", "S", "E", "W"};
        String[] colors = {"Red", "Green", "Yellow", "Blue", "Pink"};
        for (int i = 0; i < cars.length; i++) {
            int speed = rand.nextInt(60) + 20; // Speed between 20 and 80 km/h
            String direction = directions[rand.nextInt(directions.length)];
            String color = colors[rand.nextInt(colors.length)];
            cars[i] = new Car(speed, direction, color);
        }

        // Track car movements for generating the report
        Map<String, Map<String, Integer>> reportData = 
        		new HashMap<>();
        for (String direction : directions) {
            reportData.put(direction, new HashMap<>());
            for (String color : colors) {
                reportData.get(direction).put(color, 0);
            }
        }

        // Simulate the intersection for a fixed number of seconds
        for (int time = 0; time < SIMULATION_TIME_SECONDS; time++) {
            System.out.println("Time: " + time + " seconds");

            // Randomly set the traffic light to green for one direction
            int randomDirectionIndex = rand.nextInt(4);
            String greenDirection = directions[randomDirectionIndex];
            System.out.println("Traffic light is green for: " + greenDirection);
            trafficLight.setGreen(true);

            // Simulate car movements and update traffic light
            for (Car car : cars) {
                if (car.getDirection().equals(greenDirection)) {
                    System.out.println("Car going " + car.getDirection() + " with speed " + car.getSpeed() + " km/h");
                    int count = reportData.get(car.getDirection()).get(car.getColor());
                    reportData.get(car.getDirection()).put(car.getColor(), count + 1);
                } else {
                    System.out.println("Car waiting at red light: " + car.getDirection());
                }
            }

            trafficLight.setGreen(false); // Set the traffic light back to red
            System.out.println("------------------");

            try {
                Thread.sleep(1000); // Wait for 1 second in the simulation
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Generate and display the report
        System.out.println("\n---------- Report ----------");
        for (String direction : directions) {
            System.out.println("Direction: " + direction);
            for (String color : colors) {
                int count = reportData.get(direction).get(color);
                System.out.println("Color " + color + ": " + count + " cars");
            }
            System.out.println("------------------");
        }
    }
}