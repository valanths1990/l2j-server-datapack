package com.l2jserver.datapack.autobots.ui;

import com.l2jserver.datapack.autobots.ui.html.HtmlAlignment;
import com.l2jserver.datapack.autobots.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UiComponents {
    public static final String TargetRadius = "trgr";
    public static final String TargetPref = "trpr";
    public static final String AttackPlayerTypeUi = "atkplt";
    public static final String KiteRadius = "ktr";
    public static final String IsKiting = "kt";
    public static final String SummonsPet = "sump";
    public static final String PetAssists = "ptass";
    public static final String PetUsesShots = "ptush";
    public static final String PetHasBuffs = "pthab";
    public static final String PrivateSellMessage = "psmsg";
    public static final String PrivateBuyMessage = "pbmsg";
    public static final String PrivateCraftMessage = "pcmsg";
    public static final String UseSkillsOnMobs = "uskom";
    public static final String UseCpPots = "ucp";
    public static final String UseQhPots = "uqhp";
    public static final String UseGhPots = "ughp";
    public static final String EditUptime = "biedu";
    public static final String CreateBotName = "crbtnm";
    public static final String CreateBotLevel = "crbtlm";
    public static final String CreateBotWeaponEnch = "crbtwe";
    public static final String CreateBotArmorEnch = "crbtae";
    public static final String CreateBotJewelEnch = "crbtje";
    public static final String ActivityNoneActive = "ana";
    public static final String ActivityUptimeActive = "aua";
    public static final String ActivityScheduleActive = "asa";
    public static final String EditThinkIteration = "thinkms";
    public static final String EditDefaultTitle = "deftit";
    public static final String EditTargetingRange = "deftgr";

    public static String textbotComponent(String componentId, String label, String variableName, String value, boolean isUnderEdit, int contentWidth, boolean isNumber) {
        return textbotComponent(componentId, label, variableName, value, isUnderEdit, 100, contentWidth, isNumber, false, HtmlAlignment.Right);
    }

    public static String textbotComponent(String componentId, String label, String variableName, String value, boolean isUnderEdit, HtmlAlignment alignment) {
        return textbotComponent(componentId, label, variableName, value, isUnderEdit, 100, 100, false, false, alignment);
    }

    public static String textbotComponent(String componentId, String label, String variableName, String value, boolean isUnderEdit, int contentWidth) {
        return textbotComponent(componentId, label, variableName, value, isUnderEdit, 100, contentWidth, false, false, HtmlAlignment.Right);
    }

    public static String textbotComponent(String componentId, String label, String variableName, String value, boolean isUnderEdit) {
        return textbotComponent(componentId, label, variableName, value, isUnderEdit, 100, 100, false, false, HtmlAlignment.Right);
    }

    public static String textbotComponent(String componentId, String label, String variableName, String value, boolean isUnderEdit, int labelWidth, int contentWidth, boolean isNumber, boolean isMulti, HtmlAlignment alignment) {
        return Util.readFileText("components/textbox_withsave.htc")
                .replace("{{label}}", label)
                .replace("{{align}}", alignment.toString())
                .replace("{{labelwidth}}", labelWidth + "")
                .replace("{{txtcontent}}", isUnderEdit ? "<td width=" + contentWidth + "><" + (isMulti ? "multiedit" : "edit") + " var=\"{{varname}}\" width=" + contentWidth + "height=14" + (isNumber ? " type=number" : "" + "></td>") : "<td>" + value + "</td>")
                .replace("{{varname}}", variableName)
                .replace("{{action}}", isUnderEdit ? "bypass admin_a b sv " + componentId + " $" + variableName : "bypass admin_a b ed " + componentId + "")
                .replace("{{actionname}}", isUnderEdit ? "Save" : "Edit");
    }

    public static String comboboxComponent(String componentId, String label, String variableName, String selectedValue, List<String> values) {
        return Util.readFileText("components/combobox_withsave.htc")
                .replace("{{label}}", label)
                .replace("{{varname}}", variableName)
                .replace("{{items}}", selectedValue+";" + values.stream().filter(s -> !s.equals(selectedValue)).collect(Collectors.joining(";")))
                .replace("{{action}}", "bypass admin_a b sv " + componentId + " $" + variableName + "")
                .replace("{{actionname}}", "Save");
    }

    public static String checkboxComponent(String componentId, String label, boolean isChecked) {
        return checkboxComponent(componentId, label, isChecked, 75);
    }

    public static String checkboxComponent(String componentId, String label, boolean isChecked, int labelWidth) {
        return Util.readFileText("components/checkbox.htc")
                .replace("{{label}}", label)
                .replace("{{componentId}}", componentId)
                .replace("{{action}}", isChecked ? "false" : "true")
                .replace("{{checked}}", isChecked ? "_checked" : "")
                .replace("{{labelwidth}}", labelWidth + "");
    }

}
