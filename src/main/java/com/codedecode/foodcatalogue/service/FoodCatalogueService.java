package com.codedecode.foodcatalogue.service;

import com.codedecode.foodcatalogue.dto.FoodCataloguePage;
import com.codedecode.foodcatalogue.dto.FoodItemDTO;
import com.codedecode.foodcatalogue.dto.Restaurant;
import com.codedecode.foodcatalogue.entity.FoodItem;
import com.codedecode.foodcatalogue.mapper.FoodItemMapper;
import com.codedecode.foodcatalogue.repository.FoodItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FoodCatalogueService {

    @Autowired
    FoodItemRepo foodItemRepo;

    @Autowired
    RestTemplate restTemplate;

    public FoodItemDTO addFoodItem(FoodItemDTO foodItemDTO) {
        FoodItem savedItem = foodItemRepo.save(FoodItemMapper.INSTANCE.mapFoodItemDTOToFoodItem(foodItemDTO));

        return FoodItemMapper.INSTANCE.mapFoodItemtoFoodItemDTO(savedItem);
    }

    public FoodCataloguePage fetchCataloguePageDetails(Integer restaurantId) {
        // food item list
        List<FoodItem> foodItemList = fetchFoodItemList(restaurantId);

        // Restaurant details from Restaurant MS-Micro Service
        Restaurant restaurant = fetchRestaurantDetailsFromRestaurantMS(restaurantId);

        return createFoodCataloguePage(foodItemList, restaurant);
    }

    private FoodCataloguePage createFoodCataloguePage(List<FoodItem> foodItemList, Restaurant restaurant) {

        FoodCataloguePage foodCataloguePage = new FoodCataloguePage();
        foodCataloguePage.setFoodItemList(foodItemList);
        foodCataloguePage.setRestaurant(restaurant);

        return  foodCataloguePage;
    }

    private Restaurant fetchRestaurantDetailsFromRestaurantMS(Integer restaurantId) {

        return restTemplate.getForObject("http://RESTAURANT-SERVICE/restaurant/fetchById/" + restaurantId, Restaurant.class );
    }

    private List<FoodItem> fetchFoodItemList(Integer restaurantId) {
        return foodItemRepo.findAllByRestaurantId(restaurantId);
    }
}


