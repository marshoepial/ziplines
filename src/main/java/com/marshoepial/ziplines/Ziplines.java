package com.marshoepial.ziplines;

import com.marshoepial.ziplines.items.ItemRegistrar;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ziplines implements ModInitializer {
    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "ziplines";
    public static final String MOD_NAME = "Ziplines";


    @Override
    public void onInitialize() {
        log(Level.INFO, "Ziplines Initializing");
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "rope"), ItemRegistrar.ROPE);
        log(Level.INFO, "Ziplines Initialization Completed");
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

}
