package com.curso._3_06_arvores;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        RedBlackTree tree = new RedBlackTree();
        int[] values = {7, 3, 18, 10, 22, 8, 11, 26};

        for (int value : values) {
            tree.insert(value);
        }

        System.out.println("Árvore inicial:");
        tree.printTree();

        int[] removals = {18, 11, 3};
        for (int key : removals) {
            System.out.println("\nRemovendo " + key + ":");
            tree.delete(key);
            tree.printTree();
        }
    }


    static class RedBlackTree {

        private static final boolean RED = true;
        private static final boolean BLACK = false;

        private final Node nil;
        private Node root;

        RedBlackTree() {
            nil = new Node(0, BLACK);
            nil.left = nil;
            nil.right = nil;
            nil.parent = nil;
            root = nil;
        }


        void insert(int key) {
            Node newNode = new Node(key, RED);
            newNode.left = nil;
            newNode.right = nil;

            Node parent = nil;
            Node current = root;
            while (current != nil) {
                parent = current;
                if (newNode.key < current.key) {
                    current = current.left;
                } else {
                    current = current.right;
                }
            }

            newNode.parent = parent;
            if (parent == nil) {
                root = newNode;
            } else if (newNode.key < parent.key) {
                parent.left = newNode;
            } else {
                parent.right = newNode;
            }

            insertFixup(newNode);
        }


        void delete(int key) {
            Node nodeToDelete = search(root, key);
            if (nodeToDelete == nil) {
                System.out.println("Chave " + key + " não encontrada.");
                return;
            }

            Node movedNode;
            Node originalColorNode = nodeToDelete;
            boolean originalColor = originalColorNode.color;

            if (nodeToDelete.left == nil) {
                movedNode = nodeToDelete.right;
                transplant(nodeToDelete, nodeToDelete.right);
            } else if (nodeToDelete.right == nil) {
                movedNode = nodeToDelete.left;
                transplant(nodeToDelete, nodeToDelete.left);
            } else {
                originalColorNode = minimum(nodeToDelete.right);
                originalColor = originalColorNode.color;
                movedNode = originalColorNode.right;

                if (originalColorNode.parent == nodeToDelete) {
                    movedNode.parent = originalColorNode;
                } else {
                    transplant(originalColorNode, originalColorNode.right);
                    originalColorNode.right = nodeToDelete.right;
                    originalColorNode.right.parent = originalColorNode;
                }

                transplant(nodeToDelete, originalColorNode);
                originalColorNode.left = nodeToDelete.left;
                originalColorNode.left.parent = originalColorNode;
                originalColorNode.color = nodeToDelete.color;
            }

            if (originalColor == BLACK) {
                deleteFixup(movedNode);
            }
        }


        void transplant(Node oldSubtree, Node newSubtree) {
            if (oldSubtree.parent == nil) {
                root = newSubtree;
            } else if (oldSubtree == oldSubtree.parent.left) {
                oldSubtree.parent.left = newSubtree;
            } else {
                oldSubtree.parent.right = newSubtree;
            }
            newSubtree.parent = oldSubtree.parent;
        }

        void printTree() {
            if (root == nil) {
                System.out.println("(Árvore vazia)");
                return;
            }
            printTree(root, "", true);
        }

        private void insertFixup(Node node) {
            while (node.parent.color == RED) {
                if (node.parent == node.parent.parent.left) {
                    Node uncle = node.parent.parent.right;
                    if (uncle.color == RED) {
                        node.parent.color = BLACK;
                        uncle.color = BLACK;
                        node.parent.parent.color = RED;
                        node = node.parent.parent;
                    } else {
                        if (node == node.parent.right) {
                            node = node.parent;
                            leftRotate(node);
                        }
                        node.parent.color = BLACK;
                        node.parent.parent.color = RED;
                        rightRotate(node.parent.parent);
                    }
                } else {
                    Node uncle = node.parent.parent.left;
                    if (uncle.color == RED) {
                        node.parent.color = BLACK;
                        uncle.color = BLACK;
                        node.parent.parent.color = RED;
                        node = node.parent.parent;
                    } else {
                        if (node == node.parent.left) {
                            node = node.parent;
                            rightRotate(node);
                        }
                        node.parent.color = BLACK;
                        node.parent.parent.color = RED;
                        leftRotate(node.parent.parent);
                    }
                }
            }
            root.color = BLACK;
        }

        private void deleteFixup(Node node) {
            while (node != root && node.color == BLACK) {
                if (node == node.parent.left) {
                    Node sibling = node.parent.right;

                    if (sibling.color == RED) {
                        sibling.color = BLACK;
                        node.parent.color = RED;
                        leftRotate(node.parent);
                        sibling = node.parent.right;
                    }

                    if (sibling.left.color == BLACK && sibling.right.color == BLACK) {
                        sibling.color = RED;
                        node = node.parent;
                    } else {

                        if (sibling.right.color == BLACK) {
                            sibling.left.color = BLACK;
                            sibling.color = RED;
                            rightRotate(sibling);
                            sibling = node.parent.right;
                        }

                        sibling.color = node.parent.color;
                        node.parent.color = BLACK;
                        sibling.right.color = BLACK;
                        leftRotate(node.parent);
                        node = root;
                    }
                } else {
                    Node sibling = node.parent.left;

                    if (sibling.color == RED) {
                        sibling.color = BLACK;
                        node.parent.color = RED;
                        rightRotate(node.parent);
                        sibling = node.parent.left;
                    }


                    if (sibling.right.color == BLACK && sibling.left.color == BLACK) {
                        sibling.color = RED;
                        node = node.parent;
                    } else {

                        if (sibling.left.color == BLACK) {
                            sibling.right.color = BLACK;
                            sibling.color = RED;
                            leftRotate(sibling);
                            sibling = node.parent.left;
                        }


                        sibling.color = node.parent.color;
                        node.parent.color = BLACK;
                        sibling.left.color = BLACK;
                        rightRotate(node.parent);
                        node = root;
                    }
                }
            }
            node.color = BLACK;
        }

        private Node search(Node current, int key) {
            while (current != nil && key != current.key) {
                if (key < current.key) {
                    current = current.left;
                } else {
                    current = current.right;
                }
            }
            return current;
        }


        private Node minimum(Node subtree) {
            while (subtree.left != nil) {
                subtree = subtree.left;
            }
            return subtree;
        }

        private void leftRotate(Node node) {
            Node newRoot = node.right;
            node.right = newRoot.left;
            if (newRoot.left != nil) {
                newRoot.left.parent = node;
            }

            newRoot.parent = node.parent;
            if (node.parent == nil) {
                root = newRoot;
            } else if (node == node.parent.left) {
                node.parent.left = newRoot;
            } else {
                node.parent.right = newRoot;
            }

            newRoot.left = node;
            node.parent = newRoot;
        }


        private void rightRotate(Node node) {
            Node newRoot = node.left;
            node.left = newRoot.right;
            if (newRoot.right != nil) {
                newRoot.right.parent = node;
            }

            newRoot.parent = node.parent;
            if (node.parent == nil) {
                root = newRoot;
            } else if (node == node.parent.right) {
                node.parent.right = newRoot;
            } else {
                node.parent.left = newRoot;
            }

            newRoot.right = node;
            node.parent = newRoot;
        }


        private void printTree(Node node, String indent, boolean last) {
            if (node != nil) {
                System.out.print(indent);
                if (last) {
                    System.out.print("R----");
                    indent += "     ";
                } else {
                    System.out.print("L----");
                    indent += "|    ";
                }

                String color = node.color == RED ? "RED" : "BLACK";
                System.out.println(node.key + "(" + color + ")");
                printTree(node.left, indent, false);
                printTree(node.right, indent, true);
            }
        }

        private static class Node {

            private final int key;
            private boolean color;
            private Node left;
            private Node right;
            private Node parent;


            Node(int key, boolean color) {
                this.key = key;
                this.color = color;
            }
        }
    }
}
