package me.kate.clans.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import me.kate.clans.ClansPlugin;

public class ItemBuilder
{
	private ItemStack is;
	private ClansPlugin plugin;
	
	/**
	 * Create a new ItemBuilder from scratch.
	 * 
	 * @param m The material to create the ItemBuilder with.
	 */
	public ItemBuilder(ClansPlugin plugin, Material m)
	{
		this.plugin = plugin;
		is = new ItemStack(m, 1);
	}

	/**
	 * Create a new ItemBuilder over an existing itemstack.
	 * 
	 * @param is The itemstack to create the ItemBuilder over.
	 */
	public ItemBuilder(ClansPlugin plugin, ItemStack is) 
	{
		this.plugin = plugin;
		this.is = is;
	}

	/**
	 * Create a new ItemBuilder from scratch.
	 * 
	 * @param m      The material of the item.
	 * @param amount The amount of the item.
	 */
	public ItemBuilder(ClansPlugin plugin, Material m, int amount)
	{
		this.plugin = plugin;
		this.is = new ItemStack(m, amount);
	}

	/**
	 * Clone the ItemBuilder into a new one.
	 * 
	 * @return The cloned instance.
	 */
	public ItemBuilder clone() 
	{
		return new ItemBuilder(plugin, is);
	}

	/**
	 * Change the durability of the item.
	 * 
	 * @param dur The durability to set it to.
	 */
	// fuck it
	@SuppressWarnings("deprecation")
	public ItemBuilder setDurability(int dur)
	{
		is.setDurability((short) dur);
		return this;
	}

	/**
	 * Set the displayname of the item.
	 * 
	 * @param name The name to change it to.
	 */
	public ItemBuilder setName(String name)
	{
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(color(name));
		is.setItemMeta(im);
		return this;
	}

	/**
	 * Add an unsafe enchantment.
	 * 
	 * @param ench  The enchantment to add.
	 * @param level The level to put the enchant on.
	 */
	public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level)
	{
		is.addUnsafeEnchantment(ench, level);
		return this;
	}
	
	public ItemBuilder setEnchanted(boolean bool)
	{
		ItemMeta im = is.getItemMeta();
		if (bool) {
			im.addEnchant(Enchantment.DURABILITY, 1, true);
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			is.setItemMeta(im);
		}
		return this;
	}

	/**
	 * Remove a certain enchant from the item.
	 * 
	 * @param ench The enchantment to remove
	 */
	public ItemBuilder removeEnchantment(Enchantment ench) 
	{
		is.removeEnchantment(ench);
		return this;
	}

	/**
	 * Set the skull owner for the item. Works on skulls only.
	 * 
	 * @param owner The name of the skull's owner.
	 */
//	public ItemBuilder setSkullOwner(int id)
//	{
//		Skin skin = plugin.getPluginConfig().getCachedSkin(id);
//		is = SkullCreator.itemFromBase64(skin.getValue());
//		return this;
//	}

	/**
	 * Add an enchant to the item.
	 * 
	 * @param ench  The enchant to add
	 * @param level The level
	 */
	public ItemBuilder addEnchant(Enchantment ench, int level)
	{
		ItemMeta im = is.getItemMeta();
		im.addEnchant(ench, level, true);
		is.setItemMeta(im);
		return this;
	}

	/**
	 * Add multiple enchants at once.
	 * 
	 * @param enchantments The enchants to add.
	 */
	public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) 
	{
		is.addEnchantments(enchantments);
		return this;
	}

	/**
	 * Sets infinity durability on the item by setting the durability to
	 * Short.MAX_VALUE.
	 */
	@Deprecated
	public ItemBuilder setInfinityDurability()
	{
		is.setDurability((byte) 3);
		return this;
	}

	/**
	 * Re-sets the lore.
	 * 
	 * @param lore The lore to set it to.
	 */
	public ItemBuilder setLore(String... lore) 
	{
		if (lore == null)
			return this;
		
		ItemMeta im = is.getItemMeta();
		im.setLore(color(Arrays.asList(lore)));
		is.setItemMeta(im);
		return this;
	}

	/**
	 * Re-sets the lore.
	 * 
	 * @param lore The lore to set it to.
	 */
	public ItemBuilder setLore(List<String> lore)
	{
		ItemMeta im = is.getItemMeta();
		im.setLore(color(lore));
		is.setItemMeta(im);
		return this;
	}

	/**
	 * Remove a lore line.
	 * 
	 * @param lore The lore to remove.
	 */
	public ItemBuilder removeLoreLine(String line) 
	{
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		if (!lore.contains(line))
			return this;
		lore.remove(line);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	/**
	 * Remove a lore line.
	 * 
	 * @param index The index of the lore line to remove.
	 */
	public ItemBuilder removeLoreLine(int index) 
	{
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		if (index < 0 || index > lore.size())
			return this;
		lore.remove(index);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	/**
	 * Add a lore line.
	 * 
	 * @param line The lore line to add.
	 */
	public ItemBuilder addLoreLine(String line) 
	{
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>();
		if (im.hasLore())
			lore = new ArrayList<>(im.getLore());
		lore.add(line);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	/**
	 * Add a lore line.
	 * 
	 * @param line The lore line to add.
	 * @param pos  The index of where to put it.
	 */
	public ItemBuilder addLoreLine(String line, int pos) 
	{
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		lore.set(pos, line);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	/**
	 * Sets the dye color on an item. <b>* Notice that this doesn't check for item
	 * type, sets the literal data of the dyecolor as durability.</b>
	 * 
	 * @param color The color to put.
	 */
	@Deprecated
	public ItemBuilder setDyeColor(DyeColor color)
	{
		// this.is.setDurability(color.getData());
		return this;
	}
	
//	public ItemBuilder setGlassColor(short color) {
//		if (!is.getType().equals(Material.STAINED_GLASS_PANE) 
//				|| !is.getType().equals(Material.STAINED_GLASS)) {
//			return this;
//		}
//		is.setDurability(color);
//		return this;
//	}

	/**
	 * Sets the dye color of a wool item. Works only on wool.
	 * 
	 * @deprecated As of version 1.2 changed to setDyeColor.
	 * @see ItemBuilder@setDyeColor(DyeColor)
	 * @param color The DyeColor to set the wool item to.
	 */
	@Deprecated
	public ItemBuilder setWoolColor(DyeColor color)
	{
//		if (!is.getType().equals(Material.WOOL))
//			return this;
//		
//		this.is.setDurability(color.getData());
		return this;
	}

	/**
	 * Sets the armor color of a leather armor piece. Works only on leather armor
	 * pieces.
	 * 
	 * @param color The color to set it to.
	 */
	public ItemBuilder setLeatherArmorColor(Color color)
	{
		try 
		{
			LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
			im.setColor(color);
			is.setItemMeta(im);
		}
		catch (ClassCastException expected) 
		{
		}
		return this;
	}

	/**
	 * Retrieves the itemstack from the ItemBuilder.
	 * 
	 * @return The itemstack created/modified by the ItemBuilder instance.
	 */
	public ItemStack toItemStack() 
	{
		return is;
	}
	
	private String color(String in) 
	{
		return ChatColor.translateAlternateColorCodes('&', in);
	}
	
	private List<String> color(List<String> in)
	{
		in.replaceAll(lore -> color(lore));
		return in;
	}
}