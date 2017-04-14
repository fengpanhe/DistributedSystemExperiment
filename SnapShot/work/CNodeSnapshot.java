package work;

/**
 * Created by he on 17-4-13.
 */
public class CNodeSnapshot {
    private String snapshotId;
    private int nodesSourceVal[] = {0,0,0};
    private boolean ijkListen[][] = {{false,false,false},{false,false,false},{false,false,false}}; // {i,j,k} * {i,j,k}
    private int channelIndex[][] = {{0,0,0},{0,0,0},{0,0,0}};// {i,j,k} * {i,j,k}
    private char ijk[] = {'i', 'j', 'k'};


    private boolean recSign[] = {false,false,false};
    private int recSnapSource[] = {
            0,0,0,
            0,0,
            0,0,
            0,0};

    public CNodeSnapshot(String snapshotId){
        this.snapshotId = snapshotId;
    }
    private static int ijkIndex(char a){
        char ijk[] = {'i','j','k'};
        for(int i=0;i<ijk.length;i++){
            if(a == ijk[i]){
                return i;
            }
        }
        return -1;
    }
    public String[] snapshotEvent(char prveNode, char node, int recNode_sourceVal) {
        int prveNodeIndex = 0;
        int nodeIndex = 0;
        int thirdNodeIndex = 0;
        //确定索引值
        for(int i = 0; i < ijk.length; i++){
            if(prveNode == ijk[i]){
                prveNodeIndex = i;
            }else if(node == ijk[i]){
                nodeIndex = i;
            } else {
                thirdNodeIndex = i;
            }
        }
        //当前节点已记录,退出
        if(this.nodesSourceVal[nodeIndex] != 0) {
            ijkListen[prveNodeIndex][nodeIndex] = false;
            return null;
        }
        //记录当前节点的资源值
        this.nodesSourceVal[nodeIndex] = recNode_sourceVal;

        if(prveNode == 'c'){
            //上一节点为c节点时
            this.ijkListen[0][nodeIndex] = true;
            this.ijkListen[1][nodeIndex] = true;
            this.ijkListen[2][nodeIndex] = true;
            for(int i = 0; i < 3; i++){
                if(nodeIndex != i && thirdNodeIndex != i){
                    prveNodeIndex = i;
                    break;
                }
            }
        } else{
            //上一节点为普通节点时
            this.ijkListen[thirdNodeIndex][nodeIndex] = true;
        }

        //产生两个快照
        String returnVal[] = new String[5];
        returnVal[0] = String.valueOf(ijk[prveNodeIndex]);//快照1的接收节点
        returnVal[1] = String.valueOf(IConstant.ijkdelay[nodeIndex][prveNodeIndex]);//通道延时
        returnVal[2] = String.valueOf(ijk[thirdNodeIndex]);//快照2的接收节点
        returnVal[3] = String.valueOf(IConstant.ijkdelay[nodeIndex][thirdNodeIndex]);//通道延时
        returnVal[4] = String.valueOf(ijk[nodeIndex]);//本节点，即记录的快照发送节点
        return returnVal;
    }

    public void sourceEvent(char sendNode, char recNode, int sourceVal) {
        int sendNodeIndex = 0;
        int recNodeIndex = 0;
        for(int i = 0; i < ijk.length; i++){
            if(sendNode == ijk[i]){
                sendNodeIndex = i;
            }else if(recNode == ijk[i]){
                recNodeIndex = i;
            }
        }
        if(this.ijkListen[sendNodeIndex][recNodeIndex]){
            this.channelIndex[sendNodeIndex][recNodeIndex] += sourceVal;
        }
    }
    public String getSnapshotId() {
        return snapshotId;
    }

    public int[] getNodesSourceVal() {
        return nodesSourceVal;
    }

    public int[][] getIjkSource() {
        return channelIndex;
    }
    public Boolean isSnapshotId(String snapshotId){
        return this.snapshotId.equals(snapshotId);
    }
    public String getStandardSnapShot(){
        String str = new String();
        str += this.getSnapshotId() + "|" ;
        str += this.nodesSourceVal[0] + "|";
        str += this.nodesSourceVal[1] + "|";
        str += this.nodesSourceVal[2] + "|";

        str += this.channelIndex[0][1] + "|";
        str += this.channelIndex[1][0] + "|";
        str += this.channelIndex[0][2] + "|";
        str += this.channelIndex[2][0] + "|";
        str += this.channelIndex[1][2] + "|";
        str += this.channelIndex[2][1];

        return str;
    }
    public Boolean setRecSnapSource(char nodeId,String snap){
        String strings[] = snap.split("\\|");
        if(strings[0].equals(this.getSnapshotId())){
            for(int i = 0; i < this.recSnapSource.length; i++){
                this.recSnapSource[i] += Integer.valueOf(strings[i+1]);
            }
            this.recSign[ijkIndex(nodeId)] = true;

            if(recSign[0] && recSign[1] && recSign[2]){
                return true;
            }
        }
        return false;
    }
    public String getRecSnapShot(){
        String str = new String();
        str += this.getSnapshotId();
        for(int i = 0; i < this.recSnapSource.length; i++){
            str += "|" + this.recSnapSource[i];
        }
        return str;
    }
}
