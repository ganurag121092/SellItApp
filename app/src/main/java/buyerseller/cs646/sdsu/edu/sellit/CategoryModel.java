package buyerseller.cs646.sdsu.edu.sellit;


public class CategoryModel {

    private String CategoryName;

    public CategoryModel() {
    }

    public CategoryModel(String categoryName) {
        CategoryName = categoryName;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
}
