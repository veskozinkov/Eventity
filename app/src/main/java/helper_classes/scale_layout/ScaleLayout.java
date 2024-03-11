package helper_classes.scale_layout;

import android.content.Context;
import android.util.DisplayMetrics;

import vz.apps.dailyevents.R;

import constants.Constants;
import helper_classes.DeviceCharacteristics;

public class ScaleLayout {

    public static void scaleVariables(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int pxWidth = DeviceCharacteristics.getWidthPx(context);
        int pxHeight = DeviceCharacteristics.getHeightPx(context);
        double density = displayMetrics.density;

        initializeScaledLayoutVariables(context, density);

        scaleVariables1(pxWidth, pxHeight, density);
        scaleVariables2(pxWidth, pxHeight, density);
        scaleVariables3(context, pxWidth, density);
    }

    private static void scaleVariables1(int pxWidth, int pxHeight, double density) {
        if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw360dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw400dp) {
            scale1(pxHeight, density, Constants.sw360dp_DEFAULT_DENSITY, Constants.sw360dp_DEFAULT_HEIGHT_PX);
        } else {
            if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw400dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw440dp) {
                scale1(pxHeight, density, Constants.sw400dp_DEFAULT_DENSITY, Constants.sw400dp_DEFAULT_HEIGHT_PX);
            } else {
                if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw440dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw480dp) {
                    scale1(pxHeight, density, Constants.sw440dp_DEFAULT_DENSITY, Constants.sw440dp_DEFAULT_HEIGHT_PX);
                } else {
                    if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw480dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw540dp) {
                        scale1(pxHeight, density, Constants.sw480dp_DEFAULT_DENSITY, Constants.sw480dp_DEFAULT_HEIGHT_PX);
                    } else {
                        if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw540dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw600dp) {
                            scale1(pxHeight, density, Constants.sw540dp_DEFAULT_DENSITY, Constants.sw540dp_DEFAULT_HEIGHT_PX);
                        } else {
                            if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw600dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw720dp) {
                                scale1(pxHeight, density, Constants.sw600dp_DEFAULT_DENSITY, Constants.sw600dp_DEFAULT_HEIGHT_PX);
                            } else {
                                if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw720dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw800dp) {
                                    scale1(pxHeight, density, Constants.sw720dp_DEFAULT_DENSITY, Constants.sw720dp_DEFAULT_HEIGHT_PX);
                                } else {
                                    if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw800dp) {
                                        scale1(pxHeight, density, Constants.sw800dp_DEFAULT_DENSITY, Constants.sw800dp_DEFAULT_HEIGHT_PX);
                                    } else {
                                        if ((double) Math.round(pxWidth / density * 100) / 100 < Constants.sw360dp) {
                                            scale1(pxHeight, density, Constants.sw320dp_DEFAULT_DENSITY, Constants.sw320dp_DEFAULT_HEIGHT_PX);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void scaleVariables2(int pxWidth, int pxHeight, double density) {
        if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw360dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw400dp) {
            scale2(pxHeight, density, Constants.sw360dp_DEFAULT_DENSITY, Constants.sw360dp_DEFAULT_HEIGHT_PX, Constants.sw360dp_DEFAULT_HEIGHT_DP);
        } else {
            if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw400dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw440dp) {
                scale2(pxHeight, density, Constants.sw400dp_DEFAULT_DENSITY, Constants.sw400dp_DEFAULT_HEIGHT_PX, Constants.sw400dp_DEFAULT_HEIGHT_DP);
            } else {
                if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw440dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw480dp) {
                    scale2(pxHeight, density, Constants.sw440dp_DEFAULT_DENSITY, Constants.sw440dp_DEFAULT_HEIGHT_PX, Constants.sw440dp_DEFAULT_HEIGHT_DP);
                } else {
                    if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw480dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw540dp) {
                        scale2(pxHeight, density, Constants.sw480dp_DEFAULT_DENSITY, Constants.sw480dp_DEFAULT_HEIGHT_PX, Constants.sw480dp_DEFAULT_HEIGHT_DP);
                    } else {
                        if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw540dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw600dp) {
                            scale2(pxHeight, density, Constants.sw540dp_DEFAULT_DENSITY, Constants.sw540dp_DEFAULT_HEIGHT_PX, Constants.sw540dp_DEFAULT_HEIGHT_DP);
                        } else {
                            if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw600dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw720dp) {
                                scale2(pxHeight, density, Constants.sw600dp_DEFAULT_DENSITY, Constants.sw600dp_DEFAULT_HEIGHT_PX, Constants.sw600dp_DEFAULT_HEIGHT_DP);
                            } else {
                                if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw720dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw800dp) {
                                    scale2(pxHeight, density, Constants.sw720dp_DEFAULT_DENSITY, Constants.sw720dp_DEFAULT_HEIGHT_PX, Constants.sw720dp_DEFAULT_HEIGHT_DP);
                                } else {
                                    if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw800dp) {
                                        scale2(pxHeight, density, Constants.sw800dp_DEFAULT_DENSITY, Constants.sw800dp_DEFAULT_HEIGHT_PX, Constants.sw800dp_DEFAULT_HEIGHT_DP);
                                    } else {
                                        if ((double) Math.round(pxWidth / density * 100) / 100 < Constants.sw360dp) {
                                            scale2(pxHeight, density, Constants.sw320dp_DEFAULT_DENSITY, Constants.sw320dp_DEFAULT_HEIGHT_PX, Constants.sw320dp_DEFAULT_HEIGHT_DP);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void scaleVariables3(Context context, int pxWidth, double density) {
        if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw360dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw400dp) {
            scale3(context.getResources().getDisplayMetrics(), density, Constants.sw360dp_DEFAULT_DENSITY, Constants.sw360dp_DEFAULT_DM_HEIGHT_PX);
        } else {
            if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw400dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw440dp) {
                scale3(context.getResources().getDisplayMetrics(), density, Constants.sw400dp_DEFAULT_DENSITY, Constants.sw400dp_DEFAULT_DM_HEIGHT_PX);
            } else {
                if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw440dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw480dp) {
                    scale3(context.getResources().getDisplayMetrics(), density, Constants.sw440dp_DEFAULT_DENSITY, Constants.sw440dp_DEFAULT_DM_HEIGHT_PX);
                } else {
                    if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw480dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw540dp) {
                        scale3(context.getResources().getDisplayMetrics(), density, Constants.sw480dp_DEFAULT_DENSITY, Constants.sw480dp_DEFAULT_DM_HEIGHT_PX);
                    } else {
                        if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw540dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw600dp) {
                            scale3(context.getResources().getDisplayMetrics(), density, Constants.sw540dp_DEFAULT_DENSITY, Constants.sw540dp_DEFAULT_DM_HEIGHT_PX);
                        } else {
                            if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw600dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw720dp) {
                                scale3(context.getResources().getDisplayMetrics(), density, Constants.sw600dp_DEFAULT_DENSITY, Constants.sw600dp_DEFAULT_DM_HEIGHT_PX);
                            } else {
                                if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw720dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw800dp) {
                                    scale3(context.getResources().getDisplayMetrics(), density, Constants.sw720dp_DEFAULT_DENSITY, Constants.sw720dp_DEFAULT_DM_HEIGHT_PX);
                                } else {
                                    if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw800dp) {
                                        scale3(context.getResources().getDisplayMetrics(), density, Constants.sw800dp_DEFAULT_DENSITY, Constants.sw800dp_DEFAULT_DM_HEIGHT_PX);
                                    } else {
                                        if ((double) Math.round(pxWidth / density * 100) / 100 < Constants.sw360dp) {
                                            scale3(context.getResources().getDisplayMetrics(), density, Constants.sw320dp_DEFAULT_DENSITY, Constants.sw320dp_DEFAULT_DM_HEIGHT_PX);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void scale1(int pxHeight, double density, double defaultDensity, int pxDefaultHeight) {
        double multiplier = defaultDensity / density;
        
        ScaledLayoutVariables.DAY_FRAGMENT_CARD_HEIGHT = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.DAY_FRAGMENT_CARD_HEIGHT) * multiplier / pxDefaultHeight));
        ScaledLayoutVariables.DAY_FRAGMENT_LIST_DIV_HEIGHT = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.DAY_FRAGMENT_LIST_DIV_HEIGHT) * multiplier / pxDefaultHeight));
        ScaledLayoutVariables.DAY_FRAGMENT_LIST_TOP_PAD = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.DAY_FRAGMENT_LIST_TOP_PAD) * multiplier / pxDefaultHeight));
        ScaledLayoutVariables.DAY_FRAGMENT_LIST_BOT_PAD = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.DAY_FRAGMENT_LIST_BOT_PAD) * multiplier / pxDefaultHeight));

        ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TEXT_SIZE = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TEXT_SIZE) * multiplier / pxDefaultHeight));
        ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_MON_TOP_MAR = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_MON_TOP_MAR) * multiplier / pxDefaultHeight));
        ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TOP_MAR = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TOP_MAR) * multiplier / pxDefaultHeight));
        ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_SUN_BOT_MAR = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_SUN_BOT_MAR) * multiplier / pxDefaultHeight));
        ScaledLayoutVariables.WEEK_FRAGMENT_CARD_TOP_PAD = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.WEEK_FRAGMENT_CARD_TOP_PAD) * multiplier / pxDefaultHeight));
        ScaledLayoutVariables.WEEK_FRAGMENT_CARD_BOT_PAD = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.WEEK_FRAGMENT_CARD_BOT_PAD) * multiplier / pxDefaultHeight));
    }

    private static void scale2(int pxHeight, double density, double defaultDensity, int pxDefaultHeight, double dpDefaultHeight) {
        if ((double) Math.round(pxHeight / density * 100) / 100 <= dpDefaultHeight) {
            double multiplier = defaultDensity / density;

            ScaledLayoutVariables.OSD_SPINNER_PAD = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.OSD_SPINNER_PAD) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.OSD_BUTTONS_HEIGHT = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.OSD_BUTTONS_HEIGHT) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.OSD_BUTTONS_TEXT_SIZE = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.OSD_BUTTONS_TEXT_SIZE) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.OSD_PROGRESS_BAR_HEIGHT = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.OSD_PROGRESS_BAR_HEIGHT) * multiplier / pxDefaultHeight));

            ScaledLayoutVariables.BUTTONS_HEIGHT = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.BUTTONS_HEIGHT) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.BUTTONS_TEXT_SIZE = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.BUTTONS_TEXT_SIZE) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.SMALL_BUTTONS_HEIGHT = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.SMALL_BUTTONS_HEIGHT) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.SMALL_BUTTONS_TEXT_SIZE = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.SMALL_BUTTONS_TEXT_SIZE) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.FAB_CUSTOM_SIZE = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.FAB_CUSTOM_SIZE) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.ICON_SIZE = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.ICON_SIZE) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.BOTTOM_NAV_ICON_SIZE = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.BOTTOM_NAV_ICON_SIZE) * multiplier / pxDefaultHeight));

            ScaledLayoutVariables.TITLE_TEXT_SIZE = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.TITLE_TEXT_SIZE) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.SUBTITLE_TEXT_SIZE = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.SUBTITLE_TEXT_SIZE) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.BG_TITLE_TEXT_SIZE_SUB_NUM = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.BG_TITLE_TEXT_SIZE_SUB_NUM) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.DIALOG_TITLE_TEXT_SIZE = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.DIALOG_TITLE_TEXT_SIZE) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.BG_DIALOG_TITLE_TEXT_SIZE_SUB_NUM = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.BG_DIALOG_TITLE_TEXT_SIZE_SUB_NUM) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.AATD_TITLE_TEXT_SIZE = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.AATD_TITLE_TEXT_SIZE) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.DAED_TITLE_TEXT_SIZE = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.DAED_TITLE_TEXT_SIZE) * multiplier / pxDefaultHeight));

            ScaledLayoutVariables.EDIT_TEXT_PAD = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.EDIT_TEXT_PAD) * multiplier / pxDefaultHeight));
            ScaledLayoutVariables.WAVE_HEADER_HEIGHT = (int) Math.round((int) Math.round(pxHeight * (((double) ScaledLayoutVariables.WAVE_HEADER_HEIGHT) * density * multiplier / pxDefaultHeight)) / density);
            ScaledLayoutVariables.SIA_SMALL_PROGRESS_BAR_HEIGHT = (int) Math.round(pxHeight * (((double) ScaledLayoutVariables.SIA_SMALL_PROGRESS_BAR_HEIGHT) * multiplier / pxDefaultHeight));
        }
    }

    private static void scale3(DisplayMetrics displayMetrics, double density, double defaultDensity, int pxDefaultDMHeight) {
        double multiplier = defaultDensity / density;

        ScaledLayoutVariables.AATD_BACKGROUND_TOP_PAD = (int) Math.round(displayMetrics.heightPixels * (((double) ScaledLayoutVariables.AATD_BACKGROUND_TOP_PAD) * multiplier / pxDefaultDMHeight));
        ScaledLayoutVariables.ACID_BACKGROUND_TOP_PAD = (int) Math.round(displayMetrics.heightPixels * (((double) ScaledLayoutVariables.ACID_BACKGROUND_TOP_PAD) * multiplier / pxDefaultDMHeight));
        ScaledLayoutVariables.ACID2_BACKGROUND_TOP_PAD = (int) Math.round(displayMetrics.heightPixels * (((double) ScaledLayoutVariables.ACID2_BACKGROUND_TOP_PAD) * multiplier / pxDefaultDMHeight));
        ScaledLayoutVariables.CHANGE_DIALOGS_BACKGROUND_TOP_PAD = (int) Math.round(displayMetrics.heightPixels * (((double) ScaledLayoutVariables.CHANGE_DIALOGS_BACKGROUND_TOP_PAD) * multiplier / pxDefaultDMHeight));
        ScaledLayoutVariables.CD_BACKGROUND_TOP_PAD = (int) Math.round(displayMetrics.heightPixels * (((double) ScaledLayoutVariables.CD_BACKGROUND_TOP_PAD) * multiplier / pxDefaultDMHeight));
        ScaledLayoutVariables.DAD_BACKGROUND_TOP_PAD = (int) Math.round(displayMetrics.heightPixels * (((double) ScaledLayoutVariables.DAD_BACKGROUND_TOP_PAD) * multiplier / pxDefaultDMHeight));
        ScaledLayoutVariables.DAED_BACKGROUND_TOP_PAD = (int) Math.round(displayMetrics.heightPixels * (((double) ScaledLayoutVariables.DAED_BACKGROUND_TOP_PAD) * multiplier / pxDefaultDMHeight));
        ScaledLayoutVariables.OSD_BACKGROUND_TOP_PAD = (int) Math.round(displayMetrics.heightPixels * (((double) ScaledLayoutVariables.OSD_BACKGROUND_TOP_PAD) * multiplier / pxDefaultDMHeight));
        ScaledLayoutVariables.PRD_BACKGROUND_TOP_PAD = (int) Math.round(displayMetrics.heightPixels * (((double) ScaledLayoutVariables.PRD_BACKGROUND_TOP_PAD) * multiplier / pxDefaultDMHeight));
        ScaledLayoutVariables.SCD_BACKGROUND_TOP_PAD = (int) Math.round(displayMetrics.heightPixels * (((double) ScaledLayoutVariables.SCD_BACKGROUND_TOP_PAD) * multiplier / pxDefaultDMHeight));

        ScaledLayoutVariables.ACID_NUM_PICKER_TOP_MAR = (int) Math.round(displayMetrics.heightPixels * (((double) ScaledLayoutVariables.ACID_NUM_PICKER_TOP_MAR) * multiplier / pxDefaultDMHeight));
        ScaledLayoutVariables.ACID_NUM_PICKER_BOT_MAR = (int) Math.round(displayMetrics.heightPixels * (((double) ScaledLayoutVariables.ACID_NUM_PICKER_BOT_MAR) * multiplier / pxDefaultDMHeight));
        ScaledLayoutVariables.SCD_ITEM_HEIGHT = (int) Math.round(displayMetrics.heightPixels * (((double) ScaledLayoutVariables.SCD_ITEM_HEIGHT) * multiplier / pxDefaultDMHeight));
        ScaledLayoutVariables.SCD_PROGRESS_BAR_SIZE = (int) Math.round(displayMetrics.heightPixels * (((double) ScaledLayoutVariables.SCD_PROGRESS_BAR_SIZE) * multiplier / pxDefaultDMHeight));
    }

    private static void initializeScaledLayoutVariables(Context context, double density) {
        ScaledLayoutVariables.DAY_FRAGMENT_CARD_HEIGHT = (int) context.getResources().getDimension(R.dimen.info_layout_height);
        ScaledLayoutVariables.DAY_FRAGMENT_LIST_DIV_HEIGHT = (int) context.getResources().getDimension(R.dimen.list_view_divider_height);
        ScaledLayoutVariables.DAY_FRAGMENT_LIST_TOP_PAD = (int) context.getResources().getDimension(R.dimen.list_view_t_padding);
        ScaledLayoutVariables.DAY_FRAGMENT_LIST_BOT_PAD = (int) context.getResources().getDimension(R.dimen.list_view_b_padding);

        ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TEXT_SIZE = (int) context.getResources().getDimension(R.dimen.weekday_text_size);
        ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_MON_TOP_MAR = (int) context.getResources().getDimension(R.dimen.monday_t_margin);
        ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TOP_MAR = (int) context.getResources().getDimension(R.dimen.other_weekdays_t_margin);
        ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_SUN_BOT_MAR = (int) context.getResources().getDimension(R.dimen.sunday_b_margin);
        ScaledLayoutVariables.WEEK_FRAGMENT_CARD_TOP_PAD = (int) context.getResources().getDimension(R.dimen.weekday_t_padding);
        ScaledLayoutVariables.WEEK_FRAGMENT_CARD_BOT_PAD = (int) context.getResources().getDimension(R.dimen.weekday_b_padding);

        ScaledLayoutVariables.OSD_SPINNER_PAD = (int) context.getResources().getDimension(R.dimen.osd_spinner_padding);
        ScaledLayoutVariables.OSD_BUTTONS_HEIGHT = (int) context.getResources().getDimension(R.dimen.osd_increase_buttons_height);
        ScaledLayoutVariables.OSD_BUTTONS_TEXT_SIZE = (int) context.getResources().getDimension(R.dimen.osd_increase_buttons_text_size);
        ScaledLayoutVariables.OSD_PROGRESS_BAR_HEIGHT = (int) context.getResources().getDimension(R.dimen.osd_progress_bar_height);

        ScaledLayoutVariables.BUTTONS_HEIGHT = (int) context.getResources().getDimension(R.dimen.buttons_height);
        ScaledLayoutVariables.BUTTONS_TEXT_SIZE = (int) context.getResources().getDimension(R.dimen.buttons_text_size);
        ScaledLayoutVariables.SMALL_BUTTONS_HEIGHT = (int) context.getResources().getDimension(R.dimen.small_buttons_height);
        ScaledLayoutVariables.SMALL_BUTTONS_TEXT_SIZE = (int) context.getResources().getDimension(R.dimen.small_buttons_text_size);
        ScaledLayoutVariables.FAB_CUSTOM_SIZE = (int) context.getResources().getDimension(R.dimen.fab_custom_size);
        ScaledLayoutVariables.ICON_SIZE = (int) context.getResources().getDimension(R.dimen.icon_size);
        ScaledLayoutVariables.BOTTOM_NAV_ICON_SIZE = (int) context.getResources().getDimension(R.dimen.b_nav_icon_size);

        ScaledLayoutVariables.TITLE_TEXT_SIZE = (int) context.getResources().getDimension(R.dimen.title_text_size);
        ScaledLayoutVariables.SUBTITLE_TEXT_SIZE = (int) context.getResources().getDimension(R.dimen.df_subtitle_text_size);
        ScaledLayoutVariables.BG_TITLE_TEXT_SIZE_SUB_NUM = (int) context.getResources().getDimension(R.dimen.bg_title_text_size_sub_number);
        ScaledLayoutVariables.DIALOG_TITLE_TEXT_SIZE = (int) context.getResources().getDimension(R.dimen.dialog_title_text_size);
        ScaledLayoutVariables.BG_DIALOG_TITLE_TEXT_SIZE_SUB_NUM = (int) context.getResources().getDimension(R.dimen.bg_dialog_title_text_size_sub_number);
        ScaledLayoutVariables.AATD_TITLE_TEXT_SIZE = (int) context.getResources().getDimension(R.dimen.aatd_title_text_size);
        ScaledLayoutVariables.DAED_TITLE_TEXT_SIZE = (int) context.getResources().getDimension(R.dimen.daed_title_text_size);

        ScaledLayoutVariables.EDIT_TEXT_PAD = (int) context.getResources().getDimension(R.dimen.edit_text_padding);
        ScaledLayoutVariables.WAVE_HEADER_HEIGHT = (int) Math.round((int) context.getResources().getDimension(R.dimen.wave_height) / density);
        ScaledLayoutVariables.SIA_SMALL_PROGRESS_BAR_HEIGHT = (int) context.getResources().getDimension(R.dimen.progress_bar_2_height);

        ScaledLayoutVariables.AATD_BACKGROUND_TOP_PAD = (int) context.getResources().getDimension(R.dimen.aatd_background_t_padding);
        ScaledLayoutVariables.ACID_BACKGROUND_TOP_PAD = (int) context.getResources().getDimension(R.dimen.acid_background_t_padding);
        ScaledLayoutVariables.ACID2_BACKGROUND_TOP_PAD = (int) context.getResources().getDimension(R.dimen.acid2_background_t_padding);
        ScaledLayoutVariables.CHANGE_DIALOGS_BACKGROUND_TOP_PAD = (int) context.getResources().getDimension(R.dimen.change_dialogs_background_t_padding);
        ScaledLayoutVariables.CD_BACKGROUND_TOP_PAD = (int) context.getResources().getDimension(R.dimen.cd_background_t_padding);
        ScaledLayoutVariables.DAD_BACKGROUND_TOP_PAD = (int) context.getResources().getDimension(R.dimen.del_acc_background_t_padding);
        ScaledLayoutVariables.DAED_BACKGROUND_TOP_PAD = (int) context.getResources().getDimension(R.dimen.daed_background_t_padding);
        ScaledLayoutVariables.OSD_BACKGROUND_TOP_PAD = (int) context.getResources().getDimension(R.dimen.osd_background_t_padding);
        ScaledLayoutVariables.PRD_BACKGROUND_TOP_PAD = (int) context.getResources().getDimension(R.dimen.prd_background_t_padding);
        ScaledLayoutVariables.SCD_BACKGROUND_TOP_PAD = (int) context.getResources().getDimension(R.dimen.scd_background_t_padding);
        ScaledLayoutVariables.ACID_NUM_PICKER_TOP_MAR = (int) context.getResources().getDimension(R.dimen.acid_num_picker_t_margin);
        ScaledLayoutVariables.ACID_NUM_PICKER_BOT_MAR = (int) context.getResources().getDimension(R.dimen.acid_num_picker_b_margin);

        ScaledLayoutVariables.SCD_ITEM_HEIGHT = (int) context.getResources().getDimension(R.dimen.country_layout_height);
        ScaledLayoutVariables.SCD_PROGRESS_BAR_SIZE = (int) context.getResources().getDimension(R.dimen.scd_progress_bar_size);
    }
}
