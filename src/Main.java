import data.DBManager;
import repository.CategoryRepository;
import ui.MyApplication;

void main() {
    DBManager db = DBManager.getInstance();
    CategoryRepository categoryRepository = new CategoryRepository(db);


    MyApplication app = new MyApplication();
    app.start();
}
