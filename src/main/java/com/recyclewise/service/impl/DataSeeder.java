package com.recyclewise.service.impl;

import com.recyclewise.model.*;
import com.recyclewise.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final WasteItemRepository wasteItemRepository;
    private final RecyclingTipRepository recyclingTipRepository;
    private final TrashStationRepository trashStationRepository;
    private final RewardRepository rewardRepository;

    @Override
    public void run(String... args) {
        seedWasteItems();
        seedTips();
        seedStations();
        seedRewards();
    }

    private void seedWasteItems() {
        if (wasteItemRepository.count() > 0) return;
        wasteItemRepository.save(WasteItem.builder().name("Plastic Bottle").category("RECYCLABLE").icon("🍶").disposalInstructions("Rinse thoroughly, remove cap & label, flatten before placing in blue recycling bin.").tips("Reuse as a water bottle before recycling.").recyclable(true).binColor("blue").build());
        wasteItemRepository.save(WasteItem.builder().name("Glass Jar").category("RECYCLABLE").icon("🫙").disposalInstructions("Rinse clean. Remove metal lids. Place in glass recycling bin.").tips("Glass is 100% recyclable forever!").recyclable(true).binColor("green").build());
        wasteItemRepository.save(WasteItem.builder().name("Cardboard Box").category("RECYCLABLE").icon("📦").disposalInstructions("Flatten completely. Remove tape. Keep dry.").tips("Wet cardboard cannot be recycled.").recyclable(true).binColor("blue").build());
        wasteItemRepository.save(WasteItem.builder().name("Aluminum Can").category("RECYCLABLE").icon("🥫").disposalInstructions("Rinse and crush. Place in metal recycling bin.").tips("Recycling aluminum uses 95% less energy!").recyclable(true).binColor("yellow").build());
        wasteItemRepository.save(WasteItem.builder().name("Food Scraps").category("ORGANIC").icon("🥦").disposalInstructions("Place in green organic/compost bin.").tips("Composting reduces methane emissions significantly.").recyclable(false).binColor("green").build());
        wasteItemRepository.save(WasteItem.builder().name("Yard Waste").category("ORGANIC").icon("🍂").disposalInstructions("Bundle branches, bag leaves, place in organic bin.").tips("Yard waste compost makes excellent garden fertilizer.").recyclable(false).binColor("green").build());
        wasteItemRepository.save(WasteItem.builder().name("Old Battery").category("HAZARDOUS").icon("🔋").disposalInstructions("NEVER in regular bins. Take to hazardous waste facility.").tips("Many stores have free battery collection points.").recyclable(false).binColor("red").build());
        wasteItemRepository.save(WasteItem.builder().name("Paint Can").category("HAZARDOUS").icon("🪣").disposalInstructions("Take to hazardous household waste facility.").tips("Donate leftover usable paint to community projects.").recyclable(false).binColor("red").build());
        wasteItemRepository.save(WasteItem.builder().name("Old Electronics").category("HAZARDOUS").icon("💻").disposalInstructions("Take to certified e-waste recycler.").tips("Electronics contain valuable recoverable materials.").recyclable(false).binColor("red").build());
        wasteItemRepository.save(WasteItem.builder().name("Styrofoam").category("GENERAL").icon("📤").disposalInstructions("Check for local drop-off foam recycling programs.").tips("Avoid buying products with styrofoam packaging.").recyclable(false).binColor("black").build());
        wasteItemRepository.save(WasteItem.builder().name("Plastic Bag").category("GENERAL").icon("🛍️").disposalInstructions("Return to supermarket collection points.").tips("One reusable bag replaces 500+ plastic bags!").recyclable(false).binColor("black").build());
        wasteItemRepository.save(WasteItem.builder().name("Newspaper").category("RECYCLABLE").icon("📰").disposalInstructions("Bundle with string. Keep dry. Put in paper recycling.").tips("Shredded paper should go in compost.").recyclable(true).binColor("blue").build());
        wasteItemRepository.save(WasteItem.builder().name("Coffee Grounds").category("ORGANIC").icon("☕").disposalInstructions("Add to home compost or green organic bin.").tips("Coffee grounds are nitrogen-rich and great for gardens!").recyclable(false).binColor("green").build());
        wasteItemRepository.save(WasteItem.builder().name("Medicine / Pills").category("HAZARDOUS").icon("💊").disposalInstructions("Return to pharmacy take-back program. Never flush.").tips("Flushing medicine pollutes waterways.").recyclable(false).binColor("red").build());
        wasteItemRepository.save(WasteItem.builder().name("Egg Carton").category("ORGANIC").icon("🥚").disposalInstructions("Cardboard cartons go in paper recycling.").tips("Cardboard egg cartons are great for seedling starters!").recyclable(true).binColor("blue").build());
        System.out.println("✅ Seeded " + wasteItemRepository.count() + " waste items");
    }

    private void seedTips() {
        if (recyclingTipRepository.count() > 0) return;
        recyclingTipRepository.save(RecyclingTip.builder().title("Rinse Before Recycling").icon("💧").description("Always rinse containers before placing in recycling. Food residue contaminates entire batches.").category("RECYCLE").impactScore(9).build());
        recyclingTipRepository.save(RecyclingTip.builder().title("Start a Home Compost").icon("🌱").description("Composting kitchen scraps reduces household waste by up to 30%.").category("COMPOST").impactScore(10).build());
        recyclingTipRepository.save(RecyclingTip.builder().title("Buy in Bulk").icon("🛒").description("Purchasing in bulk reduces packaging waste significantly.").category("REDUCE").impactScore(8).build());
        recyclingTipRepository.save(RecyclingTip.builder().title("Repair Before Replacing").icon("🔧").description("Before throwing something away, consider if it can be repaired.").category("REUSE").impactScore(9).build());
        recyclingTipRepository.save(RecyclingTip.builder().title("Flatten Cardboard").icon("📦").description("Always flatten cardboard boxes before recycling to save sorting space.").category("RECYCLE").impactScore(6).build());
        recyclingTipRepository.save(RecyclingTip.builder().title("Choose Reusable Bags").icon("👜").description("One durable bag can replace 500+ single-use plastic bags.").category("REDUCE").impactScore(7).build());
        recyclingTipRepository.save(RecyclingTip.builder().title("Donate Unwanted Items").icon("🎁").description("Before discarding, consider donating to charities or thrift stores.").category("REUSE").impactScore(8).build());
        recyclingTipRepository.save(RecyclingTip.builder().title("Separate Hazardous Waste").icon("⚠️").description("Batteries, electronics, paint need special disposal. Never mix with regular waste.").category("RECYCLE").impactScore(10).build());
        System.out.println("✅ Seeded " + recyclingTipRepository.count() + " tips");
    }

    private void seedStations() {
        if (trashStationRepository.count() > 0) return;
        trashStationRepository.save(TrashStation.builder().name("Central Park Station").address("Jl. Sudirman No. 1, Central Park").district("Central").operatingHours("Mon-Sat 07:00-18:00").icon("🏢").active(true).latitude(-6.2088).longitude(106.8456).build());
        trashStationRepository.save(TrashStation.builder().name("Kebon Jeruk Eco Hub").address("Jl. Panjang No. 45, Kebon Jeruk").district("West").operatingHours("Mon-Sat 08:00-17:00").icon("🌿").active(true).latitude(-6.1944).longitude(106.7829).build());
        trashStationRepository.save(TrashStation.builder().name("Kemang Green Point").address("Jl. Kemang Raya No. 12, Kemang").district("South").operatingHours("Tue-Sun 09:00-16:00").icon("♻").active(true).latitude(-6.2607).longitude(106.8147).build());
        trashStationRepository.save(TrashStation.builder().name("Kelapa Gading RecycleHub").address("Jl. Boulevard Raya No. 88, Kelapa Gading").district("North").operatingHours("Mon-Fri 08:00-17:00").icon("🗑").active(true).latitude(-6.1601).longitude(106.9009).build());
        trashStationRepository.save(TrashStation.builder().name("Cibubur Waste Center").address("Jl. Alternatif Cibubur No. 5").district("East").operatingHours("Mon-Sat 07:00-16:00").icon("🏭").active(true).latitude(-6.3622).longitude(106.8924).build());
        trashStationRepository.save(TrashStation.builder().name("Menteng Eco Station").address("Jl. HOS Cokroaminoto No. 22, Menteng").district("Central").operatingHours("Mon-Sat 08:00-18:00").icon("🌍").active(true).latitude(-6.1983).longitude(106.8317).build());
        System.out.println("✅ Seeded " + trashStationRepository.count() + " stations");
    }

    private void seedRewards() {
        if (rewardRepository.count() > 0) return;
        // Food & Drinks
        rewardRepository.save(Reward.builder().name("Free Coffee Voucher").location("All city government cafeterias, Mon-Fri 07:00-15:00").description("One free coffee at any participating government cafeteria.").pointsCost(100).category("FOOD_DRINKS").icon("☕").provider("City Government Cafeteria").available(true).stockRemaining(200).build());
        rewardRepository.save(Reward.builder().name("Free Lunch Box").location("City Nutrition Program centers, Mon-Fri 11:00-13:00").description("Free nutritious lunch from the government meal program.").pointsCost(250).category("FOOD_DRINKS").icon("🍱").provider("City Nutrition Program").available(true).stockRemaining(100).build());
        rewardRepository.save(Reward.builder().name("Healthy Snack Pack").location("City Health Department offices, collect within 7 days").description("A curated pack of healthy snacks and drinks.").pointsCost(150).category("FOOD_DRINKS").icon("🥗").provider("City Health Department").available(true).stockRemaining(150).build());
        // Transport
        rewardRepository.save(Reward.builder().name("Bus Pass (1 Day)").location("Transjakarta all routes, valid any day").description("Free unlimited bus travel for one day on all city routes.").pointsCost(200).category("TRANSPORT").icon("🚌").provider("City Transport Authority").available(true).stockRemaining(500).build());
        rewardRepository.save(Reward.builder().name("MRT Credit — 20,000 IDR").description("20,000 IDR top-up credit for MRT card.").pointsCost(300).category("TRANSPORT").icon("🚇").provider("City MRT").available(true).stockRemaining(300).build());
        rewardRepository.save(Reward.builder().name("Weekly Transit Pass").description("7-day unlimited travel on all public transport.").pointsCost(800).category("TRANSPORT").icon("🎫").provider("City Transport Authority").available(true).stockRemaining(100).build());
        // Bill Discounts
        rewardRepository.save(Reward.builder().name("Water Bill Discount — 10%").description("10% discount applied to your next monthly water bill.").pointsCost(400).category("BILLS").icon("💧").provider("City Water Authority").available(true).stockRemaining(200).build());
        rewardRepository.save(Reward.builder().name("Electricity Discount — 5%").description("5% discount on your next electricity bill.").pointsCost(500).category("BILLS").icon("⚡").provider("City Electric Company").available(true).stockRemaining(200).build());
        rewardRepository.save(Reward.builder().name("Water + Electricity Bundle").description("Combined 10% discount on both water and electricity bills.").pointsCost(850).category("BILLS").icon("🏠").provider("City Government").available(true).stockRemaining(50).build());
        System.out.println("✅ Seeded " + rewardRepository.count() + " rewards");
    }
}
