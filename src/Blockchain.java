import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    private static Blockchain instance = null;

    int prefix = 2;
    public List<Block> blockchain;
    String prefixString;

    // Singleton
    private Blockchain(int prefix){
        this.prefix = prefix;
        this.blockchain = new ArrayList<>();
        this.prefixString = new String(new char[prefix]).replace('\0', '0');
    }

    public static Blockchain getInstance(int prefix){
        if (instance == null) {
            instance = new Blockchain(prefix);
        }
        return instance;
    }

    public boolean verify() {
        boolean flag = true;
        for (int i = 0; i < blockchain.size(); i++){
            String previousHash = i==0 ? StringUtil.zeros(64) : blockchain.get(i-1).getHash();
            flag = blockchain.get(i).getHash().equals(blockchain.get(i).hash())
                    && previousHash.equals(blockchain.get(i).getPreviousHash())
                    && blockchain.get(i).getHash().substring(0, prefix).equals(prefixString);
            if (!flag) break;
        }
        return flag;
    }

    public Block addBlock(Transaction data) {
        Block block;
        if (blockchain.size() == 0) {
            block = Block.genesisBlock(data);
        } else {
            block = Block.newBlock(blockchain.get(blockchain.size() - 1).getHash(),data);
        }
        block.mine(this.prefix);
        blockchain.add(block);
        return block;
    }

}
