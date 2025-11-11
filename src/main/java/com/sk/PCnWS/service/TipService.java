package com.sk.PCnWS.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class TipService {

    // A list of our helpful care tips
    private final List<String> CARE_TIPS = List.of(
        "Most houseplants prefer indirect sunlight. If leaves start turning yellow, try moving your plant to a spot with less direct sun.",
        "Overwatering is the most common way to kill a houseplant. Always check if the top 2 inches of soil are dry before watering.",
        "Plants need to be fertilized! Feed your plants every 4-6 weeks during the spring and summer growing season.",
        "Dust on leaves can block sunlight. Gently wipe your plant's leaves with a damp cloth every few months to help it breathe.",
        "Repot your plants every 12-18 months. If you see roots growing out of the drainage holes, it's time for a bigger home!",
        "Yellow leaves can mean overwatering, while brown, crispy leaves often mean underwatering.",
        "Make sure your pots have drainage holes! This prevents water from sitting at the bottom, which can cause root rot."
    );

    /**
     * Gets a new tip every day.
     * This logic uses the day of the year to pick a tip, so it's
     * consistent for all users and changes daily.
     */
    // Create a Random object once for the service
private final Random random = new Random();

/**
 * Gets a RANDOM tip every time it's called.
 */
    public String getDailyTip() {
        // Pick a random number between 0 and (list size - 1)
        int tipIndex = random.nextInt(CARE_TIPS.size());

        return CARE_TIPS.get(tipIndex);
    }
}