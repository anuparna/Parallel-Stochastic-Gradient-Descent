package parallel_sgd.dataObjects;

public class UserData {
	int user_id;
	int item_id;
	int rating;
	public UserData(int user_id, int item_id, int rating) {
		super();
		this.user_id = user_id;
		this.item_id = item_id;
		this.rating = rating;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getItem_id() {
		return item_id;
	}
	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
}
