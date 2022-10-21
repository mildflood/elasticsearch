import { TreeNode } from 'primeng/api';

export interface Treenode {
    data?: any;
    children?: TreeNode[];
    leaf?: boolean;
    expanded?: boolean;
}
