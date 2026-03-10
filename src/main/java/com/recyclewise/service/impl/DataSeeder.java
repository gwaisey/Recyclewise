package com.recyclewise.service.impl;

import com.recyclewise.model.RecyclingTip;
import com.recyclewise.model.WasteItem;
import com.recyclewise.repository.RecyclingTipRepository;
import com.recyclewise.repository.WasteItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with initial demo data on startup.
 *
 * SOLID — (S) Single Responsibility: exists only to populate initial data.
 *         Completely separate from service logic.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final WasteItemRepository wasteItemRepository;
    private final RecyclingTipRepository recyclingTipRepository;

    @Override
    public void run(String... args) {
        if (wasteItemRepository.count() > 0) return;

        // --- Waste Items ---
        wasteItemRepository.save(WasteItem.builder().name("Plastic Bottle").category("RECYCLABLE").icon("🍶")
                .disposalInstructions("Rinse thoroughly, remove cap & label, flatten before placing in blue recycling bin.")
                .tips("Reuse as a water bottle before recycling. Avoid single-use plastics when possible.").recyclable(true).binColor("blue").build());

        wasteItemRepository.save(WasteItem.builder().name("Glass Jar").category("RECYCLABLE").icon("🫙")
                .disposalInstructions("Rinse clean. Remove metal lids (recycle separately). Place in glass recycling bin.")
                .tips("Glass is 100% recyclable forever without losing quality!").recyclable(true).binColor("green").build());

        wasteItemRepository.save(WasteItem.builder().name("Cardboard Box").category("RECYCLABLE").icon("📦")
                .disposalInstructions("Flatten completely. Remove tape and staples. Keep dry. Place in paper/cardboard bin.")
                .tips("Wet cardboard cannot be recycled. Store in a dry place.").recyclable(true).binColor("blue").build());

        wasteItemRepository.save(WasteItem.builder().name("Aluminum Can").category("RECYCLABLE").icon("🥫")
                .disposalInstructions("Rinse and crush to save space. Place in metal recycling bin.")
                .tips("Recycling aluminum uses 95% less energy than producing new aluminum!").recyclable(true).binColor("yellow").build());

        wasteItemRepository.save(WasteItem.builder().name("Food Scraps").category("ORGANIC").icon("🥦")
                .disposalInstructions("Collect in a sealed container. Place in green organic/compost bin or your home compost.")
                .tips("Composting food scraps reduces methane emissions from landfills significantly.").recyclable(false).binColor("green").build());

        wasteItemRepository.save(WasteItem.builder().name("Yard Waste").category("ORGANIC").icon("🍂")
                .disposalInstructions("Bundle branches, bag leaves, place in organic waste bin or drop-off composting site.")
                .tips("Yard waste compost makes excellent garden fertilizer.").recyclable(false).binColor("green").build());

        wasteItemRepository.save(WasteItem.builder().name("Old Battery").category("HAZARDOUS").icon("🔋")
                .disposalInstructions("NEVER put in regular bins. Take to designated hazardous waste facility or electronics store drop-off.")
                .tips("Many supermarkets and electronics shops have free battery collection points.").recyclable(false).binColor("red").build());

        wasteItemRepository.save(WasteItem.builder().name("Paint Can").category("HAZARDOUS").icon("🪣")
                .disposalInstructions("Take to hazardous household waste facility. Never pour down drain or in general bin.")
                .tips("Donate leftover usable paint to community projects or charities.").recyclable(false).binColor("red").build());

        wasteItemRepository.save(WasteItem.builder().name("Old Electronics").category("HAZARDOUS").icon("💻")
                .disposalInstructions("Take to certified e-waste recycler or retailer take-back program. Never in general waste.")
                .tips("Electronics contain valuable materials like gold and copper that can be recovered.").recyclable(false).binColor("red").build());

        wasteItemRepository.save(WasteItem.builder().name("Styrofoam").category("GENERAL").icon("📤")
                .disposalInstructions("Most curbside programs don't accept styrofoam. Check for local drop-off foam recycling programs.")
                .tips("Avoid buying products with styrofoam packaging when possible.").recyclable(false).binColor("black").build());

        wasteItemRepository.save(WasteItem.builder().name("Plastic Bag").category("GENERAL").icon("🛍️")
                .disposalInstructions("Return to supermarket collection points. Do NOT put in curbside recycling bins.")
                .tips("Switch to reusable bags — one reusable bag can replace 500+ plastic bags over its lifetime!").recyclable(false).binColor("black").build());

        wasteItemRepository.save(WasteItem.builder().name("Newspaper").category("RECYCLABLE").icon("📰")
                .disposalInstructions("Bundle with string or place in paper bag. Put in paper recycling bin. Keep dry.")
                .tips("Shredded paper should go in compost, not recycling — it's too small for sorting machines.").recyclable(true).binColor("blue").build());

        wasteItemRepository.save(WasteItem.builder().name("Egg Carton").category("ORGANIC").icon("🥚")
                .disposalInstructions("Cardboard cartons go in paper recycling. Styrofoam cartons go in general waste or foam drop-off.")
                .tips("Clean cardboard egg cartons are great for seedling starters!").recyclable(true).binColor("blue").build());

        wasteItemRepository.save(WasteItem.builder().name("Medicine / Pills").category("HAZARDOUS").icon("💊")
                .disposalInstructions("Return to pharmacy take-back program. Never flush or put in household trash.")
                .tips("Flushing medicine pollutes waterways and harms aquatic life.").recyclable(false).binColor("red").build());

        wasteItemRepository.save(WasteItem.builder().name("Coffee Grounds").category("ORGANIC").icon("☕")
                .disposalInstructions("Add directly to home compost or green organic bin. Excellent worm food.")
                .tips("Coffee grounds are nitrogen-rich and help aerate compost. Great as garden fertilizer!").recyclable(false).binColor("green").build());

        // --- Recycling Tips ---
        recyclingTipRepository.save(RecyclingTip.builder().title("Rinse Before Recycling").icon("💧")
                .description("Always rinse containers before placing them in recycling. Food residue contaminates entire batches of recyclables, sending them to landfill instead.").category("RECYCLE").impactScore(9).build());

        recyclingTipRepository.save(RecyclingTip.builder().title("Start a Home Compost").icon("🌱")
                .description("Composting kitchen scraps and garden waste reduces household waste by up to 30% and creates nutrient-rich soil for your garden.").category("COMPOST").impactScore(10).build());

        recyclingTipRepository.save(RecyclingTip.builder().title("Buy in Bulk").icon("🛒")
                .description("Purchasing in bulk reduces packaging waste significantly. Bring your own containers to zero-waste stores to eliminate packaging entirely.").category("REDUCE").impactScore(8).build());

        recyclingTipRepository.save(RecyclingTip.builder().title("Repair Before Replacing").icon("🔧")
                .description("Before throwing something away, consider if it can be repaired. Repair cafes and online tutorials make fixing items easier than ever.").category("REUSE").impactScore(9).build());

        recyclingTipRepository.save(RecyclingTip.builder().title("Flatten Cardboard").icon("📦")
                .description("Always flatten cardboard boxes before recycling. Unflattened boxes take up truck space and can jam sorting machinery at recycling facilities.").category("RECYCLE").impactScore(6).build());

        recyclingTipRepository.save(RecyclingTip.builder().title("Choose Reusable Bags").icon("👜")
                .description("Switching to reusable shopping bags eliminates hundreds of plastic bags per year. One durable bag can replace 500+ single-use plastic bags.").category("REDUCE").impactScore(7).build());

        recyclingTipRepository.save(RecyclingTip.builder().title("Donate Unwanted Items").icon("🎁")
                .description("Before discarding clothing, furniture, or electronics, consider donating to charities, thrift stores, or community groups who can give items a second life.").category("REUSE").impactScore(8).build());

        recyclingTipRepository.save(RecyclingTip.builder().title("Separate Hazardous Waste").icon("⚠️")
                .description("Batteries, electronics, paint, and chemicals need special disposal. Mixing them with regular waste contaminates recycling and harms the environment.").category("RECYCLE").impactScore(10).build());

        System.out.println("✅ RecycleWise seeded: " + wasteItemRepository.count() + " items, " + recyclingTipRepository.count() + " tips.");
    }
}
