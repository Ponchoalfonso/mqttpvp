package io.github.ponchoalfonso.behavior;

import io.github.ponchoalfonso.MQTTPvP;
import io.github.ponchoalfonso.mqtt.events.SensorMessageEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * <h1>Class SensorMessageListener</h1>
 * A type of Bukkit Listener to handle data in a custom event called SensorMessageEvent.<br>
 * The event carries the values of four different sensors:
 * <ul>
 *     <li>Light sensor.</li>
 *     <li>Touch sensor.</li>
 *     <li>Temperature sensor.</li>
 *     <li>Sound sensor.</li>
 * </ul>
 * An specific behavior will be executed depending on the sensor type and the sensor value.
 *
 * @author Alfonso Valencia
 * @version 1.0
 * @see io.github.ponchoalfonso.mqtt.events.SensorMessageEvent
 */
public class SensorMessageListener implements Listener {

    MQTTPvP plugin;

    /**
     * Creates a new instance of SensorMessageListener.<br>
     * Yoy may pass a new instance of this class in the main Plugin class in order to register the events defined here.
     * @param plugin An instance of the main plugin class. It is used to get and use the server information.
     */
    public SensorMessageListener(MQTTPvP plugin) {
        this.plugin = plugin;
    }

    /**
     * An event handler method which will execute the right behavior depending on the sensor type.
     * @param event An instance of a custom event called SensorMessageEvent. It carries the sensor measured value.
     */
    @EventHandler
    public void onSensorMessageArrive(SensorMessageEvent event) {
        String sensorName = event.getSensorName();
        String sensorValue = event.getMessage();

        // Choosing the right behavior depending on which sensor sent its measured vale.
        if (sensorName.equals("light"))
            setTime(sensorValue);
        else if (sensorName.equals("touch"))
            randomPotionEffect(sensorValue);
        else if (sensorName.equals("temperature"))
            heatBurn(sensorValue);
        else if (sensorName.equals("sound"))
            warCry(sensorValue);
        else
            Bukkit.getLogger().warning("Sensor Message Arrive: Unrecognized sensor: "+ sensorName);
    }

    /**
     * Changes the Minecraft Server current time depending on the light sensor value.
     * @param lightValue The light sensor measured value.
     */
    private void setTime(String lightValue) {
        // Parsing the string into the required (and expected) datatype
        int light = Integer.parseInt(lightValue);
    }

    /**
     * Applies a random Minecraft Potion effect to all the players in the server when the touch sensor is touched.
     * @param touchValue The value of whether the touch sensor was touched or not.
     */
    private void randomPotionEffect(String touchValue) {
        // Parsing the string into the required (and expected) datatype
        boolean touch = Integer.parseInt(touchValue) == 1;
        // Pool table of potion effects
        final PotionEffectType[] effects = new PotionEffectType[] {
                PotionEffectType.ABSORPTION, PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION,
                PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.DOLPHINS_GRACE, PotionEffectType.FIRE_RESISTANCE,
                PotionEffectType.GLOWING, PotionEffectType.HARM, PotionEffectType.HEAL,
                PotionEffectType.HEALTH_BOOST, PotionEffectType.HUNGER, PotionEffectType.INCREASE_DAMAGE,
                PotionEffectType.JUMP, PotionEffectType.LEVITATION, PotionEffectType.POISON,
                PotionEffectType.REGENERATION, PotionEffectType.INVISIBILITY, PotionEffectType.SLOW,
                PotionEffectType.SLOW_FALLING, PotionEffectType.SPEED, PotionEffectType.WEAKNESS,
                PotionEffectType.WATER_BREATHING, PotionEffectType.WITHER,
        };

        if (touch) {
            Random r = new Random();
            int effectIdx, duration;

            // Giving every player a random potion effect from the effects pool with a random duration between 6s and 9s
            for(Player player : plugin.getServer().getOnlinePlayers()) {
                effectIdx = r.nextInt(effects.length);
                duration = (r.nextInt(6) + 4) * 20;

                player.addPotionEffect(new PotionEffect(effects[effectIdx], duration, 1));
            }
        }
    }

    /**
     * Sets all the players in the server on fire for 5 seconds only if temperature is over 29oC.
     * @param temperatureValue The temperature sensor measured value.
     */
    private void heatBurn(String temperatureValue) {
        // Parsing the string into the required (and expected) datatype
        float temperature = Float.parseFloat(temperatureValue);

        // Setting al players on fire for 5 seconds ONLY if temperature is over 29oC
        if (temperature >= 29f) {
            for(Player player : plugin.getServer().getOnlinePlayers()) {
                player.setFireTicks(100);
            }
        }
    }

    /**
     * Make all the enemies within 10 blocks from players run away from players for 4s only
     * if the sound level is over 600.
     * @param soundValue The sound sensor measured value.
     */
    private void warCry(String soundValue) {
        // Parsing the string into the required (and expected) datatype
        int sound = Integer.parseInt(soundValue);

        if (sound >= 600) {
            // Checking every entity near every player within 10 blocks
            for(Player player : plugin.getServer().getOnlinePlayers()) {
                for(Entity entity: player.getNearbyEntities(10, 10, 10)) {
                    // Make sure entity is a monster.
                    if (entity instanceof Monster) {
                        // Using metadata to know whether the monster is already scared and avoid scaring the
                        // same monster several times because of different players near the same monster.
                        if (entity.getMetadata("scared").isEmpty())
                            entity.setMetadata("scared", new FixedMetadataValue(plugin, false));

                        // Making monster flee
                        boolean scared = entity.getMetadata("scared").get(0).asBoolean();
                        if (!scared)
                            flee((Monster) entity);
                    }
                }
            }
        }
    }

    /**
     * Make monster run in the opposite direction they're facing.<br>
     * Note: Because of how the behavior is implemented:
     * <ul>
     *     <li>Creepers may explode after running.</li>
     *     <li>Ranged enemies might attack to nothing then run.</li>
     *     <li>Ranged enemies might run then attack to nothing.</li>
     * </ul>
     * @param monster The monster type entity that will be scared.
     */
    private void flee(final Monster monster) {
        // Get monster's location and setting its metadata as scared
        Location location = monster.getLocation();
        monster.setMetadata("scared", new FixedMetadataValue(plugin, true));

        // Generate an invulnerable, invisible and silent villager 20 blocks behind the monster.
        final Villager villager = (Villager) monster.getWorld().spawnEntity(
                location.add(location.getDirection().multiply(-20)),
                EntityType.VILLAGER
        );

        villager.setSilent(true);
        villager.setInvulnerable(true);
        villager.addPotionEffect(new PotionEffect(
                PotionEffectType.INVISIBILITY, 100,
                0, false, false
        ));
        // Give the monster speed to make it run faster
        monster.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED, 80,
                3, false, false
        ));
        // Make the monster target the villager for the next 4s
        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                i++;
                if (i == 4) {
                    // After 4 seconds set monster as not scared and remove the villager
                    monster.setMetadata("scared", new FixedMetadataValue(plugin, false));
                    villager.remove();
                    cancel();
                }
                monster.setTarget(villager);
            }
        }.runTaskTimer(plugin, 20, 20);
    }
}
