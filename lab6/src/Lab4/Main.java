package Lab4;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // 1. ArrayList
        System.out.println("1. ArrayList (unordered):");
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("Report"));
        documents.add(new Document("Notes"));
        documents.add(new Document("Report")); // Дублікат
        documents.add(new Document("Diary"));
        documents.forEach(doc -> System.out.println("- " + doc.getTitle()));

        // 2. HashSet (унікальні елементи) + hashCode()
        System.out.println("\n2. HashSet (unique elements):");
        Set<Document> uniqueDocs = new HashSet<>(documents);
        uniqueDocs.forEach(doc -> System.out.println("- " + doc.getTitle() + uniqueDocs));

        // 3. TreeSet + Comparable (сортування)
        System.out.println("\n3. TreeSet (sorted unique):");
        Set<Document> sortedDocs = new TreeSet<>(uniqueDocs);
        sortedDocs.forEach(doc -> System.out.println("- " + doc.getTitle()));

        // 4. TreeMap + Comparable (відображення)
        System.out.println("\n4. TreeMap (sorted key-value):");
        Map<String, Document> docMap = new TreeMap<>();
        sortedDocs.forEach(doc -> docMap.put(doc.getTitle(), doc));
        docMap.forEach((key, value) -> System.out.println("- " + key + ": " + value.getCreatedAt()));

        // 5. LinkedList
        System.out.println("\n5. LinkedList operations:");
        LinkedList<User> users = new LinkedList<>();
        users.add(new User("Bob", true));
        users.add(new User("Alice", false));
        users.addFirst(new User("Admin", true));
        users.addLast(new User("Guest", false));

        System.out.println("First: " + users.getFirst().getUsername());
        System.out.println("Last: " + users.getLast().getUsername());
        users.removeFirst();
        System.out.println("After removeFirst: " + users.getFirst().getUsername());

        // 6. Queue (LinkedList як черга)
        System.out.println("\n6. Queue (FIFO):");
        Queue<User> userQueue = new LinkedList<>(users);
        while (!userQueue.isEmpty()) {
            User u = userQueue.poll();
            System.out.println("Processing: " + u.getUsername());
        }

        // 7. PriorityQueue + Comparable (пріоритетна черга)
        System.out.println("\n7. PriorityQueue (natural ordering):");
        PriorityQueue<User> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(new User("Charlie", false));
        priorityQueue.add(new User("Alice", true));
        priorityQueue.add(new User("Bob", false));

        while (!priorityQueue.isEmpty()) {
            System.out.println("Next: " + priorityQueue.poll().getUsername());
        }
    }
}