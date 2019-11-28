import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Java8
{
    public void IsNull()
    {
        String str = "Li";
        int flag = Optional.ofNullable(str).orElse("").length();
        System.out.println(flag);
    }

    public void ForEach()
    {
        List<String> list = new ArrayList<>();
        list.add("aaaaaa");
        list.add("bbbbbbbbbbb");
        list.add("ccccccc");
        Optional.ofNullable(list).orElse(new ArrayList<>()).forEach(p -> System.out.println(p));
    }

    @Test
    public void TestBinaryTree()
    {
        /**
         *
         *                10
         *               /  \
         *             3     20
         *            /\     /\
         *             4    15 90
         *
         * */
        Tree tree = new Tree();
        tree.Insert(10);
        tree.Insert(3);
        tree.Insert(4);
        tree.Insert(20);
        tree.Insert(15);
        tree.Insert(90);
        System.out.println("先序遍历:");
        tree.DLR(tree.root);
        System.out.println("中序遍历:");
        tree.LDR(tree.root);
        System.out.println("后序遍历:");
        tree.LRD(tree.root);
        System.out.println("查找到的:" + tree.Find(20));
        System.out.println("删除节点后:");
        tree.Del(tree.root, 3);
        tree.LDR(tree.root);
    }

    public class BinaryNode
    {
        public int data;
        public BinaryNode leftChild;
        public BinaryNode rightChild;
        public BinaryNode parent;

        public BinaryNode(int data)
        {
            this.data = data;
        }

        @Override
        public String toString()
        {
            return "BinaryNode{根结点=" + data + ", 左子树->" + leftChild + ", 右子树->" + rightChild + '}';
        }
    }

    public class Tree
    {
        public BinaryNode root;

        public BinaryNode Find(int value)
        {
            BinaryNode current = root;
            while (value != current.data)
            {
                //小于根的值与左子树与比较
                if (value < current.leftChild.data)
                {
                    current = current.leftChild;
                }
                //否则与右子树比较
                else
                {
                    current = current.rightChild;
                }
            }
            return current;
        }

        public void Insert(int value)
        {
            BinaryNode newNode = new BinaryNode(value);
            if (root == null)
            {
                root = newNode;
                return;
            }
            else
            {
                BinaryNode current = root;
                BinaryNode parent;
                while (true)
                {
                    parent = current;
                    //左子树不空,则左子树上所有结点均小于它根结点的值
                    if (current.data > value)
                    {
                        current = current.leftChild;
                        if (current == null)
                        {
                            parent.leftChild = newNode;
                            return;
                        }
                    }
                    //右子树不空,则右子树上所有结点均大于它根结点的值
                    else
                    {
                        current = current.rightChild;
                        if (current == null)
                        {
                            parent.rightChild = newNode;
                            return;
                        }
                    }
                }
            }
        }

        /**
         * 查找大于该结点的最小结点,即查找binaryNode的后继结点
         *
         * @return
         */
        public BinaryNode SearchGtCurrent(BinaryNode binaryNode)
        {
            //如果存在右节点,则binaryNode的后继节点为右节点为根的子树的最小结点
            if (binaryNode.rightChild != null)
            {
                return MinNum(binaryNode.rightChild);
            }
            //如果没有右节点,则有以下两种可能:
            //1、binaryNode为左节点,则binaryNode的后继节点为它的父结点
            //2、binaryNode为右节点,则查找binaryNode的最小的父结点,并且该父节点要具有左子节点,这个最小的父结点就是binaryNode的后继节点
            BinaryNode result = binaryNode.parent;
            while (result != null && binaryNode == result.rightChild)
            {
                binaryNode = result;
                result = result.parent;
            }
            return result;
        }

        /**
         * 查找小于该结点的最大结点,即查找binaryNode的前继结点
         *
         * @param binaryNode
         * @return
         */
        public BinaryNode SearchLtCurrent(BinaryNode binaryNode)
        {
            if (binaryNode.leftChild != null)
            {
                return MaxNum(binaryNode.leftChild);
            }
            BinaryNode result = binaryNode.parent;
            while (result != null && binaryNode == result.leftChild)
            {
                binaryNode = result;
                result = result.parent;
            }
            return result;
        }

        /**
         * 查找最小的结点
         *
         * @param binaryNode
         * @return
         */
        public BinaryNode MinNum(BinaryNode binaryNode)
        {
            while (binaryNode.leftChild != null)
            {
                binaryNode = binaryNode.leftChild;
            }
            return binaryNode;
        }

        /**
         * 查找最大的结点
         *
         * @param binaryNode
         * @return
         */
        public BinaryNode MaxNum(BinaryNode binaryNode)
        {
            while (binaryNode.rightChild != null)
            {
                binaryNode = binaryNode.rightChild;
            }
            return binaryNode;
        }

        /**
         * 查找二叉树中值为value的节点
         *
         * @param binaryNode
         * @param value
         * @return
         */
        public BinaryNode BinaryChildSearch(BinaryNode binaryNode, int value)
        {
            if (binaryNode == null)
            {
                return binaryNode;
            }
            if (binaryNode.leftChild != null && value < binaryNode.leftChild.data)
            {
                return BinaryChildSearch(binaryNode.leftChild, value);
            }
            else if (binaryNode.rightChild != null && value > binaryNode.rightChild.data)
            {
                return BinaryChildSearch(binaryNode.rightChild, value);
            }
            else
            {
                return binaryNode;
            }
        }

        /**
         * 二叉的树节点删除
         */
        public BinaryNode Del(BinaryNode binaryNode, int value)
        {
            //子节点
            BinaryNode x = null;
            //删除结点
            BinaryNode y = null;

            BinaryNode z = BinaryChildSearch(binaryNode, value);
            //只有一个节了或者没有节点时
            if (z.leftChild == null || z.rightChild == null)
            {
                //z就是要删除的节点
                y = z;
            }
            else
            {
                y = SearchGtCurrent(z);
            }
            //获取子节点,不管左右
            if (y.leftChild != null)
            {
                x = y.leftChild;
            }
            else
            {
                x = y.rightChild;
            }
            //如果存在子节点,就关联被删节点的父节点
            if (x != null)
            {
                x.parent = y.parent;
            }
            //如果父节点为空,说明要删的是根节点
            if (y.parent == null)
            {
                //设置根节点
                root = x;
            }
            //要删除的是左节点
            else if (y == y.parent.leftChild)
            {
                //左节点关联子节点
                y.parent.leftChild = x;
            }
            //要删除的是右节点
            else
            {
                //右节点关联子节点
                y.parent.rightChild = x;
            }
            //如果要删的节点和一开始传入的不一样
            if (y != z)
            {
                z.data = y.data;
            }
            return y;
        }

        /**
         * 先序遍历
         *
         * @param binaryNode
         */
        public void DLR(BinaryNode binaryNode)
        {
            System.out.println(binaryNode.data + " ");
            if (binaryNode.leftChild != null)
            {
                DLR(binaryNode.leftChild);
            }
            if (binaryNode.rightChild != null)
            {
                DLR(binaryNode.rightChild);
            }
        }

        /**
         * 后序遍历
         *
         * @param binaryNode
         */
        public void LRD(BinaryNode binaryNode)
        {
            if (binaryNode.leftChild != null)
            {
                LRD(binaryNode.leftChild);
            }
            if (binaryNode.rightChild != null)
            {
                LRD(binaryNode.rightChild);
            }
            System.out.println(binaryNode.data + " ");
        }

        /**
         * 中序遍历
         *
         * @param binaryNode
         */
        public void LDR(BinaryNode binaryNode)
        {
            if (binaryNode.leftChild != null)
            {
                LDR(binaryNode.leftChild);
            }
            System.out.println(binaryNode.data + " ");
            if (binaryNode.rightChild != null)
            {
                LDR(binaryNode.rightChild);
            }
        }
    }

}
