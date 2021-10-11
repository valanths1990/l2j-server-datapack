package com.l2jserver.datapack.custom.augment;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.data.xml.impl.OptionData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.L2Augmentation;
import com.l2jserver.gameserver.model.PageResult;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.options.Options;
import com.l2jserver.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import com.l2jserver.gameserver.network.serverpackets.ExShowVariationMakeWindow;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jserver.gameserver.util.HtmlUtil;
import com.mysql.cj.protocol.x.ConfinedInputStream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Augment implements IBypassHandler {
    private static final String[] COMMANDS = {"augment;homepage"};
    private static final String STANDARD_ICON = "icon.skill3123";
    private static final String optionButtonAndImage = " <tr><td width=\"36\" align=\"center\" height=\"50\">\n" +
            "                        <img src=\"%icon%\" width=\"32\" height=\"32\">\n" +
            "                    </td>\n" +
            "                    <td width=\"300\" align=\"center\" height=\"50\">\n" +
            "                        <button value=\"%name%\" action=\"bypass augment;homepage set %id%\" width=300 height=32\n" +
            "                                back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">\n" +
            "                    </td>\n" +
            "                    <td width=\"36\" align=\"center\" height=\"50\">\n" +
            "                        <img src=\"%icon%\" width=\"32\" height=\"32\">\n" +
            "                    </td></tr>";
    private final Map<L2PcInstance, OptionHolder> playersOptions = new ConcurrentHashMap<>();

    @Override
    public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
        String[] split = command.split(" ");
        String html = "";
        if (split.length == 1) {
            html = getHomepage(activeChar);
        }
        if (split.length == 2) {
            if (split[1].equals("add")) {
                activeChar.sendPacket(ExShowVariationMakeWindow.STATIC_PACKET);
            } else if (split[1].equals("remove")) {
                activeChar.sendPacket(ExShowVariationCancelWindow.STATIC_PACKET);
            } else if (split[1].equals("optionone")) {
                html = getStatOptions(activeChar);
            } else if (split[1].equals("optiontwo")) {
                html = getPassiveAndActiveOptions(activeChar);
            } else if (split[1].equals("reset")) {
                playersOptions.remove(activeChar);
                html = getHomepage(activeChar);
            } else if (split[1].equals("augment")) {
                augmentWeapon(activeChar);
                html = getHomepage(activeChar);
            }
        }
        if (split.length == 3) {
            if (split[1].equals("set")) {

                int id = Integer.parseInt(split[2]);
                playersOptions.computeIfAbsent(activeChar, k -> new OptionHolder());
                if (com.l2jserver.datapack.custom.augment.Options.getTypeById(id) == OptionsType.STAT) {
                    playersOptions.get(activeChar).optionOne = id;
                } else {
                    playersOptions.get(activeChar).optionTwo = id;
                }
                html = getHomepage(activeChar);
            }
        }
        CommunityBoardHandler.separateAndSend(html, activeChar);
        return false;
    }

    private void augmentWeapon(L2PcInstance pc) {
        L2ItemInstance currency = pc.getInventory().getItemByItemId(Configuration.customs().getAugmentationItemId());
        long amount = Configuration.customs().getAugmentationItemAmount();
        if (currency == null || currency.getCount() < amount) {
            pc.sendMessage("You don't have enough Golden Apiga to augment your Weapon.");
            return;
        }

        if (!playersOptions.containsKey(pc) || playersOptions.get(pc).optionOne == 0 || playersOptions.get(pc).optionTwo == 0) {
            pc.sendMessage("You need first to choose the Options before starting Augmentation.");
            return;
        }
        L2ItemInstance item = pc.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
        if (item == null || item.isAugmented() || item.isHeroItem() || !item.isWeapon()) {
            pc.sendMessage("You are not wearing a Weapon.");
            return;
        }
        int optOne = playersOptions.get(pc).optionOne;
        int optTwo = playersOptions.get(pc).optionTwo;
        L2Augmentation augmentation = new L2Augmentation(((optTwo << 16) + optOne));
        item.setAugmentation(augmentation);
        pc.destroyItem("Augmentation", currency, amount, null, true);
        pc.sendPacket(new InventoryUpdate());
    }

    private String getPassiveAndActiveOptions(L2PcInstance pc) {
        String html =
                HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/augment/option.html");
        PageResult passiveSkills = HtmlUtil.createPage(com.l2jserver.datapack.custom.augment.Options.getOptoinByType(OptionsType.PASSIVE)
                , com.l2jserver.datapack.custom.augment.Options.values().length
                , 0
                , com.l2jserver.datapack.custom.augment.Options.values().length
                , i -> ""
                , o -> {
                    Options opt = OptionData.getInstance().getOptions(o.getId());
                    if (opt == null) {
                        return "";
                    }
                    return optionButtonAndImage.replaceAll("%icon%", opt.getPassiveSkill().getSkill().getIcon())
                            .replace("%id%", String.valueOf(o.getId()))
                            .replace("%name%", opt.getPassiveSkill().getSkill().getName());

                });
        PageResult activeSkills = HtmlUtil.createPage(com.l2jserver.datapack.custom.augment.Options.getOptoinByType(OptionsType.ACTIVE)
                , com.l2jserver.datapack.custom.augment.Options.values().length
                , 0
                , com.l2jserver.datapack.custom.augment.Options.values().length
                , i -> ""
                , o -> {
                    Options opt = OptionData.getInstance().getOptions(o.getId());
                    if (opt == null) {
                        return "";
                    }
                    return optionButtonAndImage.replaceAll("%icon%", opt.getActiveSkill().getSkill().getIcon())
                            .replace("%id%", String.valueOf(o.getId()))
                            .replace("%name%", opt.getActiveSkill().getSkill().getName());
                });

        html = html.replaceFirst("%optionsList%", passiveSkills.getBodyTemplate().toString())
                .replace("%optionsList%", activeSkills.getBodyTemplate().toString());
        html = html.replaceFirst("%type%", "Passive Skills").replace("%type%", "Active Skills");

        return html;
    }

    private String getStatOptions(L2PcInstance pc) {
        String html =
                HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/augment/option.html");

        PageResult pr = HtmlUtil.createPage(com.l2jserver.datapack.custom.augment.Options.getOptoinByType(OptionsType.STAT)
                , com.l2jserver.datapack.custom.augment.Options.values().length
                , 0
                , com.l2jserver.datapack.custom.augment.Options.values().length
                , i -> ""
                , o -> {
                    Options opt = OptionData.getInstance().getOptions(o.getId());
                    if (opt == null) {
                        return "";
                    }
                    return optionButtonAndImage.replaceAll("%icon%", STANDARD_ICON)
                            .replace("%id%", String.valueOf(o.getId()))
                            .replace("%name%", o.getDescr());

                });
        html = html.replaceFirst("%optionsList%", pr.getBodyTemplate().toString());
        html = html.replaceFirst("%type%", "Bonus Stats");
        return html;
    }

    private String getHomepage(L2PcInstance pc) {
        String html = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/augment/homepage.html");
        String optIcon = STANDARD_ICON;
        String optName = "";
        if (playersOptions.containsKey(pc)) {
            OptionHolder opt = playersOptions.get(pc);
            Options one = OptionData.getInstance().getOptions(opt.optionOne);
            Options two = OptionData.getInstance().getOptions(opt.optionTwo);

            if (one != null) {
                if (one.hasFuncs()) {
                    optIcon = STANDARD_ICON;
                    optName = com.l2jserver.datapack.custom.augment.Options.getOptionById(opt.optionOne).getDescr();
                }
                if (one.hasActiveSkill()) {
                    optIcon = one.getActiveSkill().getSkill().getIcon();
                    optName = one.getActiveSkill().getSkill().getName();
                }
                if (one.hasPassiveSkill()) {
                    optIcon = one.getPassiveSkill().getSkill().getIcon();
                    optName = one.getPassiveSkill().getSkill().getName();
                }
            }
            html = html.replaceAll("%optionOneIcon%", optIcon)
                    .replaceFirst("%optionOneName%", optName);

            optIcon = STANDARD_ICON;
            optName = "";
            if (two != null) {
                if (two.hasFuncs()) {
                    optIcon = STANDARD_ICON;
                    optName = com.l2jserver.datapack.custom.augment.Options.getOptionById(opt.optionOne).getDescr();
                }
                if (two.hasActiveSkill()) {
                    optIcon = two.getActiveSkill().getSkill().getIcon();
                    optName = two.getActiveSkill().getSkill().getName();
                }
                if (two.hasPassiveSkill()) {
                    optIcon = two.getPassiveSkill().getSkill().getIcon();
                    optName = two.getPassiveSkill().getSkill().getName();
                }
            }
            html = html.replaceAll("%optionTwoIcon%", optIcon)
                    .replace("%optionTwoName%", optName);
        }

        html = html.replaceAll("%optionOneIcon%", optIcon)
                .replaceAll("%optionTwoIcon%", optIcon);

        L2Item item = ItemTable.getInstance().getTemplate(Configuration.customs().getAugmentationItemId());
        html = html.replaceFirst("%itemName%", item.getName())
                .replace("%price%", String.valueOf(Configuration.customs().getAugmentationItemAmount() / 1000))
                .replace("%itemIcon%", item.getIcon());

        return html;
    }

    private static final class OptionHolder {
        int optionOne = 0;
        int optionTwo = 0;
    }

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }
}
