import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

//BDD definition
class BDD {
    Integer depthTree = 0;
    Integer numberNodes = 0;
    
    Node root;
    String order;
    
    static class Node {
        // Integer val;
        // Integer id;
        int depth;
        
        Node parent;
        
        Node left;
        Node right;
        
        char character;
        String function;
        Integer id;
        
        Boolean isLeaf;
        
        Integer leftValue;
        Integer rightValue;
        
        Node(int depth, Node left, Node right, Node parent, char character, String function, Integer id, Boolean isLeaf,
        Integer leftValue, Integer rightValue) {
            this.depth = depth;
            this.left = left;
            this.right = right;
            this.parent = parent;
            this.character = character;
            this.function = function;
            
            this.id = id;
            
            this.isLeaf = isLeaf;
            this.leftValue = leftValue;
            this.rightValue = rightValue;
            
        }
        
    }
}

class Start {
    // Check if we can do reduciton or not
    static int doReduction = 0;
    
    static String bfunction = "";
    static String order = "";
    
    static Integer reducedNodes = 0;
    static Integer numberOfNodes = 0;
    
    // Spravit to tak, ze budem hladat pre kazdeu formulu prehladavat po stringoch a
    // ak najdem +,.,^,* tak pre kazdy operand budem mat definove co robi napr pre +
    // budem mat to ze sa vytvoria prve 3 nody a potm v dalsich budem to mat taktiez
    // len inak definovane a budem to robim pre kazdu pravu aj lavu vetvu a
    // budem musiet to kontrolvoat stale.
    
    // BDD_create -> vytvori strom z boolean funkcie
    // BDD_use -> posle 0101010 -> A:0, B:1, C:0 ..... a vyhodnoti na zaklade stromu
    
    public static boolean findCharInString(char c, String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                return true;
            }
        }
        return false;
    }
    
    // Find someting like B+!B.... if oposite exist ot is always true
    public static boolean findBooleanAndNegation(String s) {
        
        if (!s.contains("!")) {
            return false;
        }
        
        String[] arrayData = s.split("\\+");
        
        for (int i = 0; i < arrayData.length; i++) {
            if (arrayData[i].length() <= 2 && arrayData[i].length() > 0) {
                String temp = arrayData[i];
                
                if (temp.charAt(0) == '!') {
                    for (int j = 0; j < arrayData.length; j++) {
                        if (arrayData[j].length() == 1) {
                            if (temp.charAt(1) == arrayData[j].charAt(0)) {
                                return true;
                            }
                        }
                    }
                } else {
                    continue;
                }
            }
        }
        
        return false;
    }
    
    // A+AB -> right side is always true || !A+AB -> left side is always true
    public static String findAloneChar(String s, char c, int a) {
        String[] arrayData = s.split("\\+");
        
        for (int i = 0; i < arrayData.length; i++) {
            if (arrayData[i].length() <= 2 && arrayData[i].length() > 0) {
                String temp = arrayData[i];
                
                if (temp.length() != 1) {
                    if (temp.charAt(0) == '!' && temp.charAt(1) == c) {
                        return "leftTrue";
                    }
                } else {
                    if (temp.charAt(0) == c) {
                        return "rightTrue";
                    }
                }
            }
        }
        
        return "null";
    }
    
    public static void print(BDD.Node root, int depth) {
        
        if (root == null) {
            System.out.println("(XXXXXX)");
            return;
        }
        
        int height = depth,
        width = (int) Math.pow(2, height - 1);
        
        // Preparing variables for loop.
        List<BDD.Node> current = new ArrayList<BDD.Node>(1),
        next = new ArrayList<BDD.Node>(2);
        current.add(root);
        
        final int maxHalfLength = 4;
        int elements = 1;
        
        StringBuilder sb = new StringBuilder(maxHalfLength * width);
        for (int i = 0; i < maxHalfLength * width; i++)
        sb.append(' ');
        String textBuffer;
        
        // Iterating through height levels.
        for (int i = 0; i < height; i++) {
            
            sb.setLength(maxHalfLength * ((int) Math.pow(2, height - 1 - i) - 1));
            
            // Creating spacer space indicator.
            textBuffer = sb.toString();
            
            // Print tree node elements
            for (BDD.Node n : current) {
                
                System.out.print(textBuffer);
                
                if (n == null) {
                    
                    System.out.print("        ");
                    next.add(null);
                    next.add(null);
                    
                } else {
                    
                    System.out.printf("(" + n.character + "/ " + n.function + " /" + n.id + ")");
                    next.add(n.left);
                    next.add(n.right);
                    
                }
                
                System.out.print(textBuffer);
                
            }
            
            System.out.println();
            // Print tree node extensions for next level.
            if (i < height - 1) {
                
                for (BDD.Node n : current) {
                    
                    System.out.print(textBuffer);
                    
                    if (n == null)
                    System.out.print("        ");
                    else
                    System.out.printf("%s      %s",
                    n.left == null ? " " : "/", n.right == null ? " " : "\\");
                    
                    System.out.print(textBuffer);
                    
                }
                System.out.println();
            }
            // Renewing indicators for next run.
            elements *= 2;
            current = next;
            next = new ArrayList<BDD.Node>(elements);
        }
    }
    
    public static String removeCharFromString(char c, String s) {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < s.length(); i++) {
            if (i + 1 < s.length()) {
                if (s.charAt(i + 1) == c) {
                    if (s.charAt(i) == '!') {
                        continue;
                    }
                }
            }
            
            if (s.charAt(i) == '+') {
                continue;
            }
            if (s.charAt(i) != c) {
                sb.append(s.charAt(i));
            }
            
        }
        
        return sb.toString();
    }
    
    // Functions like A.!A
    public static Boolean findCharInStringWithoutNegation(char c, String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                Boolean check = i - 1 >= 0 ? true : false;
                
                if (check) {
                    if (Character.compare('!', s.charAt(i - 1)) == 0) {
                        continue;
                    }
                }
                
                return true;
            }
        }
        return false;
    }
    
    
    
    public static int getRandomNumberWithInterval(int min, int max) {
        Random random = new Random();
        int randomNumber = random.nextInt((max - min) + 1) + min;
        return randomNumber;
    }
    
    // Creates BDD tree
    public static BDD BDD_create(String bfunction, String order) {
        BDD.Node default_left = new BDD.Node(0, null, null, null, '0', "0", null, true, 0, 0);
        BDD.Node default_right = new BDD.Node(0, null, null, null, '1', "1", null, true, 1, 1);
        
        BDD BDD = new BDD();
        BDD.depthTree = 0;
        BDD.order = order;
        
        String[] arrayData = bfunction.split("\\+");
        String[] arrayDataForOtherNodes;
        
        // String subOrder = "";
        int counterOrder = 0;
        String subOrder;
        
        Boolean found = false;
        Boolean simplify = false;
        
        if (BDD.depthTree == 0) {
            // Create root node for BDD
            BDD.Node RootNode = new BDD.Node(BDD.depthTree, null, null, null, order.charAt(0), order, BDD.numberNodes,
            false, 0, 1);
            
            BDD.root = RootNode;
            BDD.root.function = bfunction;
            
            BDD.root.left = default_left;
            BDD.root.right = default_right;
            
            BDD.numberNodes++;
            
        }
        
        if (findBooleanAndNegation(bfunction)) {
            
            BDD.root.left = new BDD.Node(BDD.depthTree, null, null, null, '1', "1", null, true, 1, 1);
            BDD.root.right = BDD.root.left;
            
            return BDD;
        }
        
        String demorgan = findAloneChar(bfunction, order.charAt(0), doReduction);
        
        if (demorgan.equals("leftTrue")) {
            BDD.root.left = default_right;
            BDD.root.right = default_left;
        }
        if (demorgan.equals("rightTrue")) {
            BDD.root.right = default_right;
        }
        
        if (demorgan.equals("rotateTrue")) {
            BDD.root.left = default_right;
            BDD.root.right = default_left;
            
            return BDD;
        }
        
        ArrayList<BDD.Node> nodesCurrentLayer = new ArrayList<BDD.Node>();
        ArrayList<BDD.Node> nodesCurrentLayer2 = new ArrayList<BDD.Node>();
        
        ArrayList<BDD.Node> tempLayerAll = new ArrayList<BDD.Node>();
        
        // Get some char from order and check if we can find this in the part of
        // bfunction which is exploded by +
        
        for (int o = 0; o < order.length(); o++) {
            char currentCharInOrder = order.charAt(o);
            
            // ?? If BDD.depthTree == 0, we are creating root node childrens, also we are
            // going to simplify tie boolean equation for them.
            if (BDD.depthTree == 0) {
                for (int i = 0; i < arrayData.length; i++) {
                    
                    String current_part = arrayData[i];
                    Integer removeTwoNegations = current_part.indexOf("!!");
                    
                    if (removeTwoNegations != -1) {
                        current_part = current_part.replace("!!", "");
                    }
                    
                    // If Boolean function is for example: A.!A then skip round
                    simplify = current_part.indexOf("!" + currentCharInOrder) != -1 ? true : false;
                    
                    if (simplify && findCharInStringWithoutNegation(currentCharInOrder, current_part)) {
                        continue;
                    }
                    
                    char currentCharInOrderCharacter;
                    
                    if (order.indexOf(currentCharInOrder) <= (order.length() - 2)) {
                        currentCharInOrderCharacter = order.charAt(o + 1);
                    } else {
                        currentCharInOrderCharacter = '$';
                    }
                    
                    String currentPartWithoutOrderChar = removeCharFromString(currentCharInOrder, current_part);
                    
                    int currentChechNegationPosition;
                    Boolean currentChechNegation;
                    
                    found = false;
                    
                    if (findCharInString(currentCharInOrder, current_part)) {
                        found = true;
                        
                        // Find negation on the left side of the order char
                        currentChechNegationPosition = current_part.indexOf(currentCharInOrder);
                        currentChechNegation = false;
                        
                        // Compare if character on the left side is negation
                        if (currentChechNegationPosition - 1 >= 0) {
                            if (Character.compare('!', current_part.charAt(currentChechNegationPosition - 1)) == 0) {
                                currentChechNegation = true;
                            }
                        }
                        
                        // Create nodes and connect them.
                        // If we have negation save node to left side of root node otherwise to right
                        if (currentChechNegation) {
                            if (BDD.root.left == null || BDD.root.left.isLeaf == true) {
                                if (currentPartWithoutOrderChar.length() >= 1) {
                                    if (!demorgan.equals("leftTrue")) {
                                        BDD.root.left = new BDD.Node((BDD.depthTree + 1), null, null, BDD.root,
                                        currentCharInOrderCharacter,
                                        currentPartWithoutOrderChar, BDD.numberNodes, false, 0, 1);
                                        
                                        if(BDD.root.right == null || BDD.root.right.function.equals("1") || BDD.root.right.function
                                        .equals("0")){
                                            BDD.root.right = new BDD.Node(BDD.depthTree, null, null, null, '0',
                                            "0",
                                            null, true, 0,
                                            0);
                                        }
                                        
                                        // Save node for another iteration
                                        nodesCurrentLayer.add(BDD.root.left);
                                        
                                        BDD.numberNodes++;
                                    }
                                }
                            } else {
                                if (currentPartWithoutOrderChar.length() >= 1) {
                                    if (!demorgan.equals("leftTrue")) {
                                        String[] chceckForSamePart = BDD.root.left.function.split("\\+");
                                        Boolean FoundSame = false;
                                        for (int j = 0; j < chceckForSamePart.length; j++) {
                                            String currentCheck = chceckForSamePart[j];
                                            if (currentCheck.equals(currentPartWithoutOrderChar)) {
                                                FoundSame = true;
                                            }
                                        }
                                        if (!FoundSame) {
                                            BDD.root.left.function = BDD.root.left.function + "+"
                                            + currentPartWithoutOrderChar;
                                        }
                                    }
                                }
                            }
                        } else {
                            if (BDD.root.right == null || BDD.root.right.isLeaf == true) {
                                if (currentPartWithoutOrderChar.length() >= 1) {
                                    if (!demorgan.equals("rightTrue")) {
                                        
                                        BDD.root.right = new BDD.Node((BDD.depthTree + 1), null, null, BDD.root,
                                        currentCharInOrderCharacter,
                                        currentPartWithoutOrderChar, BDD.numberNodes, false, 0, 1);
                                        
                                        // Save node for another iteration
                                        nodesCurrentLayer.add(BDD.root.right);
                                        
                                        BDD.numberNodes++;
                                    }
                                }
                            } else {
                                if (currentPartWithoutOrderChar.length() >= 1) {
                                    if (!demorgan.equals("rightTrue")) {
                                        
                                        String[] chceckForSamePart = BDD.root.right.function.split("\\+");
                                        Boolean FoundSame = false;
                                        for (int j = 0; j < chceckForSamePart.length; j++) {
                                            String currentCheck = chceckForSamePart[j];
                                            if (currentCheck.equals(currentPartWithoutOrderChar)) {
                                                FoundSame = true;
                                            }
                                        }
                                        if (!FoundSame) {
                                            
                                            BDD.root.right.function = BDD.root.right.function + "+"
                                            + currentPartWithoutOrderChar;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if (!found) {
                        if (BDD.root.left == null || BDD.root.left.isLeaf == true) {
                            if (currentPartWithoutOrderChar.length() >= 1) {
                                if (!demorgan.equals("leftTrue")) {
                                    
                                    BDD.root.left = new BDD.Node((BDD.depthTree + 1), null, null, BDD.root, currentCharInOrderCharacter, currentPartWithoutOrderChar, BDD.numberNodes,  false, 0, 1);
                                    
                                    // Save node for another iteration
                                    nodesCurrentLayer.add(BDD.root.left);
                                    
                                    BDD.numberNodes++;
                                }
                            }
                        } else {
                            if (currentPartWithoutOrderChar.length() >= 1) {
                                if (!demorgan.equals("leftTrue")) {
                                    
                                    String[] chceckForSamePart = BDD.root.left.function.split("\\+");
                                    Boolean FoundSame = false;
                                    for (int j = 0; j < chceckForSamePart.length; j++) {
                                        String currentCheck = chceckForSamePart[j];
                                        if (currentCheck.equals(currentPartWithoutOrderChar)) {
                                            FoundSame = true;
                                        }
                                    }
                                    if (!FoundSame) {
                                        BDD.root.left.function = BDD.root.left.function + "+"
                                        + currentPartWithoutOrderChar;
                                    }
                                }
                            }
                        }
                        
                        if (BDD.root.right == null || BDD.root.right.isLeaf == true) {
                            if (currentPartWithoutOrderChar.length() >= 1) {
                                if (!demorgan.equals("rightTrue")) {
                                    
                                    BDD.root.right = new BDD.Node((BDD.depthTree + 1), null, null, BDD.root,
                                    currentCharInOrderCharacter, currentPartWithoutOrderChar, BDD.numberNodes,
                                    false,
                                    0, 1);
                                    
                                    // Save node for another iteration
                                    nodesCurrentLayer.add(BDD.root.right);
                                    
                                    BDD.numberNodes++;
                                }
                            }
                        } else {
                            if (currentPartWithoutOrderChar.length() >= 1) {
                                if (!demorgan.equals("rightTrue")) {
                                    String[] chceckForSamePart = BDD.root.right.function.split("\\+");
                                    Boolean FoundSame = false;
                                    for (int j = 0; j < chceckForSamePart.length; j++) {
                                        String currentCheck = chceckForSamePart[j];
                                        if (currentCheck.equals(currentPartWithoutOrderChar)) {
                                            FoundSame = true;
                                        }
                                    }
                                    if (!FoundSame) {
                                        BDD.root.right.function = BDD.root.right.function + "+"
                                        + currentPartWithoutOrderChar;
                                    }
                                }
                            }
                        }
                    }
                }
                
                // !! DO reduction
                if (doReduction != 0) {
                    nodesCurrentLayer2 = removeDuplicatesFromArrayList(nodesCurrentLayer);
                    
                    for (int index = 0; index < 1; index++) {
                        BDD.Node CheckReduction = reduction(nodesCurrentLayer2, BDD.root.left);
                        
                        if (CheckReduction != null) {
                            BDD.root.left = CheckReduction;
                        }
                        
                        CheckReduction = reduction(nodesCurrentLayer2, BDD.root.right);
                        
                        if (CheckReduction != null) {
                            BDD.root.right = CheckReduction;
                        }
                    }
                }
                
            } else {
                // ?? If root with childrens are created build the tree
                
                ArrayList<BDD.Node> tempLayer = new ArrayList<BDD.Node>();
                ArrayList<BDD.Node> tempLayerParents = new ArrayList<BDD.Node>();
                ArrayList<BDD.Node> tempLayer2 = new ArrayList<BDD.Node>();
                
                for (BDD.Node node : nodesCurrentLayer) {
                    
                    found = false;
                    node.left = default_left;
                    node.right = default_right;
                    
                    // Find something like A+!A
                    if (findBooleanAndNegation(node.function)) {
                        
                        node.left = default_right;
                        node.right = node.left;
                        
                        continue;
                    }
                    
                    // Find something like A+!A
                    demorgan = findAloneChar(node.function, currentCharInOrder, doReduction);
                    
                    if (demorgan.equals("leftTrue")) {
                        node.left = default_right;
                        node.right = default_left;
                    }
                    if (demorgan.equals("rightTrue")) {
                        node.right = default_right;
                    }
                    
                    if (demorgan.equals("rotateTrue")) {
                        node.left = default_right;
                        node.right = default_left;
                        
                        continue;
                    }
                    
                    //ADD parents to arraylist for reduction check
                    tempLayerParents.add(node);
                    
                    arrayDataForOtherNodes = node.function.split("\\+");
                    
                    String current_part;
                    
                    for (int i = 0; i < arrayDataForOtherNodes.length; i++) {
                        current_part = arrayDataForOtherNodes[i];
                        
                        // If Boolean function is for example: A.!A then skip round
                        simplify = current_part.indexOf("!" + currentCharInOrder) != -1 ? true : false;
                        
                        if (simplify && findCharInStringWithoutNegation(currentCharInOrder, current_part)) {
                            continue;
                        }
                        
                        char currentCharInOrderCharacter;
                        
                        String currentPartWithoutOrderChar = removeCharFromString(currentCharInOrder, current_part);
                        
                        if (order.indexOf(currentCharInOrder) <= (order.length() - 2)) {
                            currentCharInOrderCharacter = order.charAt(o + 1);
                        } else {
                            currentCharInOrderCharacter = '$';
                        }
                        
                        found = false;
                        
                        if (findCharInString(currentCharInOrder, current_part)) {
                            found = true;
                            
                            int currentChechNegationPosition;
                            Boolean currentChechNegation;
                            // Find negation on the left side of the order char
                            currentChechNegationPosition = current_part.indexOf(currentCharInOrder);
                            currentChechNegation = false;
                            
                            // Compare if character on the left side is negation
                            if (currentChechNegationPosition - 1 >= 0) {
                                if (Character.compare('!',
                                current_part.charAt(currentChechNegationPosition - 1)) == 0) {
                                    currentChechNegation = true;
                                }
                            }
                            // Create nodes and connect them.
                            // If we have negation save node to left side of root node otherwise to right
                            if (currentChechNegation) {
                                if (node.left == null || node.left.isLeaf == true) {
                                    if (currentPartWithoutOrderChar.length() >= 1) {
                                        if (!demorgan.equals("leftTrue")) {
                                            
                                            node.left = new BDD.Node((BDD.depthTree + 1), null, null, node,
                                            currentCharInOrderCharacter,
                                            currentPartWithoutOrderChar, BDD.numberNodes, false, 0, 1);
                                            
                                            
                                            if(node.right == null || node.right.function.equals("1") || node.right.function.equals("0")){
                                                node.right = new BDD.Node((BDD.depthTree + 1), null, null, null, '0', "0",
                                                null, true, 0,
                                                0);
                                            }
                                            
                                            // Save node for another iteration
                                            tempLayer.add(node.left);
                                            
                                            BDD.numberNodes++;
                                        }
                                    }
                                } else {
                                    if (currentPartWithoutOrderChar.length() >= 1) {
                                        if (!demorgan.equals("leftTrue")) {
                                            String[] chceckForSamePart = node.left.function.split("\\+");
                                            Boolean FoundSame = false;
                                            for (int j = 0; j < chceckForSamePart.length; j++) {
                                                String currentCheck = chceckForSamePart[j];
                                                if (currentCheck.equals(currentPartWithoutOrderChar)) {
                                                    FoundSame = true;
                                                }
                                            }
                                            if (!FoundSame) {
                                                node.left.function = node.left.function + "+"
                                                + currentPartWithoutOrderChar;
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (node.right == null || node.right.isLeaf == true) {
                                    if (currentPartWithoutOrderChar.length() >= 1) {
                                        if (!demorgan.equals("rightTrue")) {
                                            
                                            node.right = new BDD.Node((BDD.depthTree + 1), null, null, node,
                                            currentCharInOrderCharacter,
                                            currentPartWithoutOrderChar, BDD.numberNodes, false, 0, 1);
                                            // Save node for another iteration
                                            tempLayer.add(node.right);
                                            
                                            BDD.numberNodes++;
                                        }
                                    }
                                } else {
                                    // check for A+A or AB+AB it is always A | AB
                                    if (currentPartWithoutOrderChar.length() >= 1) {
                                        if (!demorgan.equals("rightTrue")) {
                                            String[] chceckForSamePart = node.right.function.split("\\+");
                                            Boolean FoundSame = false;
                                            for (int j = 0; j < chceckForSamePart.length; j++) {
                                                String currentCheck = chceckForSamePart[j];
                                                if (currentCheck.equals(currentPartWithoutOrderChar)) {
                                                    FoundSame = true;
                                                }
                                            }
                                            if (!FoundSame) {
                                                node.right.function = node.right.function + "+"
                                                + currentPartWithoutOrderChar;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        if (!found) {
                            if (node.left == null || node.left.isLeaf == true) {
                                if (!demorgan.equals("leftTrue")) {
                                    node.left = new BDD.Node((BDD.depthTree + 1), null, null, node,
                                    currentCharInOrderCharacter, currentPartWithoutOrderChar,
                                    BDD.numberNodes, false,
                                    0, 1);
                                    // Save node for another iteration
                                    tempLayer.add(node.left);
                                    
                                    BDD.numberNodes++;
                                }
                            } else {
                                if (currentPartWithoutOrderChar.length() >= 1) {
                                    if (!demorgan.equals("leftTrue")) {
                                        String[] chceckForSamePart = node.left.function.split("\\+");
                                        Boolean FoundSame = false;
                                        for (int j = 0; j < chceckForSamePart.length; j++) {
                                            String currentCheck = chceckForSamePart[j];
                                            if (currentCheck.equals(currentPartWithoutOrderChar)) {
                                                FoundSame = true;
                                            }
                                        }
                                        if (!FoundSame) {
                                            node.left.function = node.left.function + "+"
                                            + currentPartWithoutOrderChar;
                                        }
                                    }
                                }
                            }
                            
                            if (node.right == null || node.right.isLeaf == true) {
                                if (!demorgan.equals("rightTrue")) {
                                    
                                    node.right = new BDD.Node((BDD.depthTree + 1), null, null, node,
                                    currentCharInOrderCharacter, currentPartWithoutOrderChar,
                                    BDD.numberNodes, false,
                                    0, 1);
                                    
                                    // Save node for another iteration
                                    tempLayer.add(node.right);
                                    
                                    BDD.numberNodes++;
                                }
                                
                            } else {
                                if (currentPartWithoutOrderChar.length() >= 1) {
                                    if (!demorgan.equals("rightTrue")) {
                                        String[] chceckForSamePart = node.right.function.split("\\+");
                                        Boolean FoundSame = false;
                                        for (int j = 0; j < chceckForSamePart.length; j++) {
                                            String currentCheck = chceckForSamePart[j];
                                            if (currentCheck.equals(currentPartWithoutOrderChar)) {
                                                FoundSame = true;
                                            }
                                        }
                                        
                                        if (!FoundSame) {
                                            node.right.function = node.right.function + "+"
                                            + currentPartWithoutOrderChar;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    //!! DO reduction
                    if(doReduction != 0){
                        tempLayer2 =  removeDuplicatesFromArrayList(tempLayer);  
                        
                        for (int index = 0; index < tempLayerParents.size(); index++) {
                            BDD.Node CheckReduction = reduction(tempLayer2, tempLayerParents.get(index).left);
                            
                            if (CheckReduction != null) {
                                tempLayerParents.get(index).left = CheckReduction;
                            }
                            
                            CheckReduction = reduction(tempLayer2, tempLayerParents.get(index).right);
                            
                            if (CheckReduction != null) {
                                tempLayerParents.get(index).right = CheckReduction;
                            }
                        }
                    }
                    
                }
                // tempLayer = reduction(tempLayer);
                nodesCurrentLayer = tempLayer;
            }
            // Increase height of diagram -> 0 is root
            BDD.depthTree++;
        }
        return BDD;
    }
    
    public static ArrayList<BDD.Node> removeDuplicatesFromArrayList(ArrayList<BDD.Node> tempLayer ){
        ArrayList<BDD.Node> newList = new ArrayList<BDD.Node>();
        Boolean addToList = true;
        
        for (BDD.Node element : tempLayer) {
            
            if(newList.size() == 0){
                newList.add(element);
                continue;
            }else{
                for (BDD.Node element2 : newList) {
                    if (element2.function.equals(element.function)) {
                        addToList = false;
                    }
                }
            }
            
            if(addToList){
                newList.add(element);
            }
            
            addToList = true;
        }
        
        return newList;
    }
    
    
    // ?? BDD_use
    public static String BDD_use(BDD BDD, String stream) {
        if (stream.length() != BDD.order.length()) {
            return "-1";
        }
        BDD.Node node = BDD.root;
        
        for (int i = 0; i < BDD.order.length(); i++) {
            char current_path = stream.charAt(i);
            if (current_path == '1') {
                if (node.right == null || node.right.function.equals("1")) {
                    return "1";
                }
                
                if (node.right.function.equals("0")) {
                    return "0";
                }
                
                if (node.right.isLeaf) {
                    if (node.right.function.equals("1")) {
                        return "1";
                    } else {
                        return "0";
                    }
                }
                
                node = node.right;
            }
            
            if (current_path == '0') {
                if (node.left == null || node.left.function.equals("0")) {
                    return "0";
                }
                
                if (node.left.function.equals("1")) {
                    return "1";
                }
                
                if (node.left.isLeaf) {
                    if (node.left.function.equals("0")) {
                        return "0";
                    } else {
                        return "1";
                    }
                }
                
                node = node.left;
            }
        }
        
        return "-1";
    }
    
    public static String removeDuplicatesCharsNegationsPlus(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            
            if (!sb.toString().contains(String.valueOf(str.charAt(i)))) {
                if (str.charAt(i) != '!') {
                    if (str.charAt(i) != '+') {
                        sb.append(str.charAt(i));
                    }
                }
            }
        }
        
        return sb.toString();
    }
    
    
    
    // ?? Reduction -> point to same node when found same bfunction in node
    public static BDD.Node reduction(ArrayList<BDD.Node> tempLayer, BDD.Node currentFunction) {
        ArrayList<BDD.Node> reduced = new ArrayList<BDD.Node>();
        BDD.Node node;
        
        for (int j = 0; j < tempLayer.size(); j++) {
            if (tempLayer.get(j).function.equals(currentFunction.function) && currentFunction.id != tempLayer.get(j).id) {
                node = tempLayer.get(j);
                
                return node;
            }
            
        }
        
        return null;
    }
    
    // Sort string
    public static String sortString(String inputString) {
        char tempArray[] = inputString.toCharArray();
        
        Arrays.sort(tempArray);
        
        return new String(tempArray);
    }
    
    
    
    public static ArrayList generateTruthTable(Integer a) {
        
        ArrayList<String> truthTable = new ArrayList<String>();
        
        int n = a;
        for (int i = 0; i != (1 << n); i++) {
            String s = Integer.toBinaryString(i);
            while (s.length() != n) {
                s = '0' + s;
            }
            
            truthTable.add(s);
        }
        
        return truthTable;
    }
    
    public static int getNumberOfNodes(int depth) {
        int numberOfNodes = 0;
        int numberOfNodesInLayer = 1;
        
        for (int i = 0; i < depth; i++) {
            numberOfNodes += (i != 0) ? numberOfNodesInLayer * 2 : 1;
            
            numberOfNodesInLayer = (i != 0) ? numberOfNodesInLayer * 2 : 1;
        }
        
        return numberOfNodes;
    }
    
    public static String GenerateBooleaFunction(int size, int length) {
        String apl = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String temp = "";
        int i = 0;
        
        int number = 0;
        while (i < length) {
            if (number >= size - 1) {
                temp = temp + "+";
                number = 0;
            }
            if (Math.round(Math.random()) == 0) {
                temp = temp + "!";
            }
            
            if(size > apl.length()){
                size = 27;
            }
            
            number = ThreadLocalRandom.current().nextInt(number, size - 1);
            temp = temp + apl.charAt(number);
            
            number++;
            i++;
        }
        
        for (int j = 0; j < size - 1; j++) {
            Boolean found = false;
            
            for (int j2 = 0; j2 < temp.length(); j2++) {
                if (apl.charAt(j) != temp.charAt(j2)) {
                    found = true;
                }
            }
            
            if (found) {
                if (Math.round(Math.random()) == 0) {
                    temp = temp + "!";
                    temp = temp + apl.charAt(j);
                }
            }
        }
        
        return temp;
    }
    public static String GenerateOrder(String bfunction, Boolean sortingOrder){
        String order = "";
        String onlyCharacterNoD = removeDuplicatesCharsNegationsPlus(bfunction);
        
        if (sortingOrder) {
            order = sortString(onlyCharacterNoD);
        } else {
            order = onlyCharacterNoD;
        }
        
        return order;
    }
    public static void BDD_test(){
        long startTime = System.nanoTime(); // Start nanoTimer

        int size = 5;
        int length = 5;
        Boolean sortOrder = true;

        long timeBDDUSE = 0;
        long timeBDDCREATE = 0;

        // Total stats
        double totalNodes = 0;
        double totalReducednodes = 0;

        for (int k = 0; k < 1000; k++) {
            // Generate random order and random Boelean function
            String fun = GenerateBooleaFunction(size, length);
            String or = GenerateOrder(fun, sortOrder);

            // totalNodes = 0;
            // totalReducednodes = 0;

            ArrayList<Integer> first_truth = new ArrayList<Integer>();
            ArrayList<Integer> second_truth = new ArrayList<Integer>();

            // Generate BDD tre with reduction and not with reduction
            for (int j = 0; j < 2; j++) {
                // if (j > 0) {
                // System.out.println("With Reduction");
                // } else {
                // System.out.println("With NO-Reduction");
                // }

                // Create BDD tree
                long startCreate = System.nanoTime();
                BDD BDD = BDD_create(fun, or);
                long endCreate = System.nanoTime();

                timeBDDCREATE += (endCreate - startCreate);

                // // // System.out.println(BDD.root.left.function);
                // ?? Print
                // print(BDD.root, BDD.depthTree + 2);

                // System.out.println("Testing combinations:" + fun + "||| Order: " + or);

                ArrayList data = generateTruthTable(BDD.order.length());

                for (int i = 0; i < data.size(); i++) {

                    long startUse = System.nanoTime();

                    String truth_value = BDD_use(BDD, data.get(i).toString());

                    long endUse = System.nanoTime();
                    timeBDDUSE += (endUse - startUse);

                    // ?? Print
                    // System.out.println(data.get(i).toString() + "->" + truth_value);
                    // System.out.print(truth_value);

                    if (j > 0) {
                        first_truth.add(Integer.parseInt(truth_value));
                    } else {
                        second_truth.add(Integer.parseInt(truth_value));
                    }
                }

                if (j > 0) {
                    numberOfNodes = getNumberOfNodes(BDD.depthTree);
                    double reducedNodes = numberOfNodes - BDD.numberNodes;

                    // double rate = ((double) reducedNodes / (double) numberOfNodes) * 100;

                    // System.out.println("Nodes after reduction: " + reducedNodes);
                    // System.out.println("Total nodes: " + numberOfNodes);

                    totalNodes += numberOfNodes;
                    totalReducednodes += reducedNodes;

                    // System.out.println("Reduction rate is: " + rate + "%");
                }

                doReduction++;
            }

            // Check if values matches
            for (int i = 0; i < first_truth.size(); i++) {
                if (first_truth.get(i) != second_truth.get(i)) {
                    System.out.println("XXXXXXXXXXXXXXXXX");
                    System.out.println("Values do not match");
                    System.out.println("XXXXXXXXXXXXXXXXX");

                    System.out.println(fun);
                    System.out.println(or);

                    System.exit(0);
                    break;
                }
            }

            doReduction = 0;
        }

        // ?? Compare all trees with reduction rate
        System.out.println("Total nodes: " + totalNodes);
        System.out.println("Reduced nodes: " + totalReducednodes);

        double rate_total = ((double) totalReducednodes / (double) totalNodes) * 100;
        System.out.println("Total reduction rate is: " + rate_total);

        long endTime = System.nanoTime();

        System.out.println("Run Time: " + (endTime - startTime) + " ns"); // Celkovy cas v ns
        System.out.println("Run Time BDD_USE: " + (timeBDDUSE) + " ns"); // Celkovy cas v ns
        System.out.println("Run Time BDD_CREATE: " + (timeBDDCREATE) + " ns"); // Celkovy cas v ns

        // System.out.println("Run Time: BDD_CREATE" + (timeBDDCREATE) / 1000000 +
        // "ns"); // Celkovy cas v ns

        long total = Runtime.getRuntime().totalMemory();
        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // Memmory ussage
        System.out.println("Total memory: " + total / 1024 + " KB");
        System.out.println("Used memory: " + used / 1024 + " KB");
        // System.out.println("Used memory: " + used / 1024 / 1024 + "KB");
    }
    public static void main(String[] args) {

        Boolean testing = true;

        if(testing){
            BDD_test();
        }else{
            //Init values
            int size = 4;
            int length = 4;
            Boolean sortOrder = true;

            //Generate funciton and order 
            String fun = GenerateBooleaFunction(size, length);
            String or = GenerateOrder(fun, sortOrder);

            //Do reduction
            doReduction = 1;

            //Create bdd tree
            BDD BDD = BDD_create(fun, or);
            print(BDD.root, BDD.depthTree + 2);


            ArrayList data = generateTruthTable(BDD.order.length());

            for (int i = 0; i < data.size(); i++) {

                String truth_value = BDD_use(BDD, data.get(i).toString());

                // ?? Print
                System.out.println(data.get(i).toString() + "->" + truth_value);
            }
        }
    }
}
