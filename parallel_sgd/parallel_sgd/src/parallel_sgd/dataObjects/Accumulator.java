package parallel_sgd.dataObjects;

import java.util.HashMap;

public class Accumulator {
	private HashMap<Integer,Features> user_features;
	private HashMap<Integer,Features> item_features;
	private int num_Features;
	
	public HashMap<Integer, Features> getUser_features() {
		return user_features;
	}
	public void setUser_features(HashMap<Integer, Features> user_features) {
		this.user_features = user_features;
	}
	public HashMap<Integer, Features> getItem_features() {
		return item_features;
	}
	public void setItem_features(HashMap<Integer, Features> item_features) {
		this.item_features = item_features;
	}
	
	public Accumulator(int num_Features) {
        this.num_Features = num_Features;
        setUser_features(new HashMap<Integer,Features>());
        setItem_features(new HashMap<Integer,Features>());
    }
    
    public synchronized Features getUserFeatures(int user_id){
        if (!user_features.containsKey(user_id)){
            user_features.put(user_id, new Features(num_Features));
        }
        return user_features.get(user_id);
    }
    
    public synchronized Features getItemFeatures(int item_id){
        if (!item_features.containsKey(item_id)){
        	item_features.put(item_id, new Features(num_Features));
        }
        return item_features.get(item_id);
    }
	public int getNum_Features() {
		return num_Features;
	}
	public void setNum_Features(int num_Features) {
		this.num_Features = num_Features;
	}
}
