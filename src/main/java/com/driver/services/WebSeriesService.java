package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        if(webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName())!=null)
            throw new Exception("Series is already present");

        WebSeries webSeries = new WebSeries();
        webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());

        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
        webSeries.setProductionHouse(productionHouse);
        productionHouse.getWebSeriesList().add(webSeries); //add webseries to the list

        int sum=0;
        for(WebSeries webSeries1 : productionHouse.getWebSeriesList()){
            sum+=webSeries1.getRating();
        }
        productionHouse.setRatings(sum/productionHouse.getWebSeriesList().size());

        //save producion and webseries
        ProductionHouse saveproductionHouse = productionHouseRepository.save(productionHouse);
        WebSeries saveWebSeries = webSeriesRepository.save(webSeries);
        return saveWebSeries.getId();
    }

}
