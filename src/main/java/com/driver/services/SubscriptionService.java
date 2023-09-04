package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
//import jdk.internal.loader.AbstractClassLoaderValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        //get user
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription(); //create subscription object and set values and save to db
        subscription.setUser(user);
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        SubscriptionType subscriptionType = subscriptionEntryDto.getSubscriptionType();
        int noOfScreenSubscribed = subscriptionEntryDto.getNoOfScreensRequired();
        int subscribedAmount =0;

        if(subscriptionType == subscriptionType.BASIC){
            subscribedAmount = 500 + 200*noOfScreenSubscribed ;
        }
        else if(subscriptionType == subscriptionType.PRO){
            subscribedAmount = 800 + 250 * noOfScreenSubscribed ;
        }
        else{
            subscribedAmount = 1000 + 350 * noOfScreenSubscribed;
        }
        subscription.setTotalAmountPaid(subscribedAmount);

        //set subscription to user
        user.setSubscription(subscription);
        //set user to subsciption
        subscription.setUser(user);
        //save parent->Automatically child will get saved
        userRepository.save(user);

        return subscribedAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get(); //get user
        Subscription currsubscription = user.getSubscription();
        if(currsubscription.getSubscriptionType() == SubscriptionType.ELITE){
            throw new Exception("Already the best Subscription");
        }

        int AmountToBePaid =0;//intially 0

        //case
        //if curr subscription is pro --> elite
        if(currsubscription.getSubscriptionType() ==SubscriptionType.PRO){
            int currAmount = currsubscription.getTotalAmountPaid();
            int amountToBeUpgrade = 1000 +(350*currsubscription.getNoOfScreensSubscribed());

            currsubscription.setSubscriptionType(SubscriptionType.ELITE);
            currsubscription.setTotalAmountPaid(amountToBeUpgrade);
            AmountToBePaid = amountToBeUpgrade - currAmount ;
            user.setSubscription(currsubscription);
            userRepository.save(user);
        }else{
            //if curr sub is BASIC--> PRO
            int currAmount = currsubscription.getTotalAmountPaid();
            int amountToBeUpgrade = 800 +(250*currsubscription.getNoOfScreensSubscribed());

            currsubscription.setSubscriptionType(SubscriptionType.PRO);
            currsubscription.setTotalAmountPaid(amountToBeUpgrade);
            AmountToBePaid = amountToBeUpgrade - currAmount ;
            user.setSubscription(currsubscription);
            userRepository.save(user);
        }

        return AmountToBePaid;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptions = subscriptionRepository.findAll();
        int totalRevenue = 0;
        for(Subscription subscription : subscriptions){
            totalRevenue+=subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
