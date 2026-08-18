package com.dscommerce.tests;

import com.dscommerce.dto.CategoryDTO;
import com.dscommerce.entities.Category;

public class CategoryFactory {

    public static Category createCategory() {
        return new Category(4L, "Sport");
    }

    public static CategoryDTO createCategoryDTO() {
        Category category = createCategory();
        return new CategoryDTO(category);
    }
}
