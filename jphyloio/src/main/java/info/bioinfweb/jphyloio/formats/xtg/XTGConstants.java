/*
 * JPhyloIO - Event based parsing and stream writing of multiple sequence alignment and tree formats. 
 * Copyright (C) 2015-2019  Ben Stöver, Sarah Wiechers
 * <http://bioinfweb.info/JPhyloIO>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.jphyloio.formats.xtg;


import info.bioinfweb.jphyloio.ReadWriteConstants;

import javax.xml.namespace.QName;



/**
 * Defines constants necessary to read <a href="http://bioinfweb.info/xmlns/xtg">XTG</a> files.
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 */
public interface XTGConstants {
	//TODO Should more or all predicate prefixes referencing their parent element be removed?
	
	// XML constants:
	public static final String XTG_FORMAT_NAME = "Extensible TreeGraph 2 format";
	public static final String XTG = "xtg";
	
  public static final String NAMESPACE_URI = "http://bioinfweb.info/xmlns/xtg";
  public static final String VERSION = "1.4";
  public static final String FULL_SCHEMA_LOCATION = NAMESPACE_URI + " " + NAMESPACE_URI + "/" + VERSION + ".xsd";
  
  public static final QName TAG_ROOT = new QName(NAMESPACE_URI, "TreegraphDocument");
  
  public static final QName TAG_GLOBAL_FORMATS = new QName(NAMESPACE_URI, "GlobalFormats");
  public static final QName ATTR_BG_COLOR = new QName("BgColor");
  public static final QName TAG_DOCUMENT_MARGIN = new QName(NAMESPACE_URI, "DocMargin");
  public static final QName ATTR_BRANCH_LENGTH_SCALE = new QName("BranchLengthScale");
  public static final QName ATTR_SHOW_SCALE_BAR = new QName("ShowScaleBar");
  public static final QName ATTR_SHOW_ROOTED = new QName("ShowRooted");
  public static final QName ATTR_ALIGN_TO_SUBTREE = new QName("AlignToSubtree");
  public static final QName ATTR_POSITION_LABELS_TO_LEFT = new QName("LabelsLeft");
  
  public static final QName TAG_NODE_BRANCH_DATA_ADAPTERS = new QName(NAMESPACE_URI, "NodeBranchDataAdapters");
  public static final QName TAG_ADAPTER = new QName(NAMESPACE_URI, "Adapter");
  public static final QName ATTR_ADAPTER_NAME = new QName("Name");
  public static final QName ATTR_ADAPTER_ID = new QName("ID");
  public static final QName ATTR_ADAPTER_PURPOSE = new QName("Purpose");
  public static final String VALUE_ADAPTER_PURPOSE_PREFIX = "info.bioinfweb.treegraph.";
  public static final String VALUE_LEAVES_ADAPTER = VALUE_ADAPTER_PURPOSE_PREFIX + "defaultLeavesAdapter";
  public static final String DEPRECATED_VALUE_LEAVES_ADAPTER = VALUE_ADAPTER_PURPOSE_PREFIX + "defaultLeafsAdapter";
  public static final String VALUE_SUPPORT_VALUES_ADAPTER = VALUE_ADAPTER_PURPOSE_PREFIX + "defaultSupportAdapter";
  
  public static final QName ATTR_WIDTH = new QName("Width");
  public static final QName ATTR_HEIGHT = new QName("Height");
  
  public static final QName ATTR_LEFT = new QName("Left");
  public static final QName ATTR_TOP = new QName("Top");
  public static final QName ATTR_RIGHT = new QName("Right");
  public static final QName ATTR_BOTTOM = new QName("Bottom");
  
  public static final QName ATTR_TEXT = new QName("Text");
  public static final QName ATTR_TEXT_IS_DECIMAL = new QName("IsDecimal");
  public static final QName ATTR_DECIMAL_FORMAT = new QName("DecimalFormat");
  public static final QName ATTR_LOCALE_LANG = new QName("LocaleLang");
  public static final QName ATTR_LOCALE_COUNTRY = new QName("LocaleCountry");
  public static final QName ATTR_LOCALE_VARIANT = new QName("LocaleVariant");
  public static final QName ATTR_TEXT_COLOR = new QName("TextColor");
  public static final QName ATTR_TEXT_HEIGHT = new QName("TextHeight");
  public static final QName ATTR_TEXT_STYLE = new QName("TextStyle");
  public static final QName ATTR_FONT_FAMILY = new QName("FontFamily");

  public static final QName ATTR_LINE_COLOR = new QName("LineColor");
  public static final QName ATTR_LINE_WIDTH = new QName("LineWidth");
  
  public static final QName ATTR_ID = new QName("Id");
  
  public static final QName TAG_TREE = new QName(NAMESPACE_URI, "Tree");
  
  public static final QName TAG_NODE = new QName(NAMESPACE_URI, "Node");
  public static final QName ATTR_UNIQUE_NAME = new QName("UniqueName");
  public static final QName ATTR_EDGE_RADIUS = new QName("EdgeRadius");
  public static final QName TAG_LEAF_MARGIN = new QName(NAMESPACE_URI, "LeafMargin");
  
  public static final QName TAG_BRANCH = new QName(NAMESPACE_URI, "Branch");
  public static final QName ATTR_BRANCH_LENGTH = new QName("Length");
  public static final QName ATTR_CONSTANT_WIDTH = new QName("ConstantWidth");
  public static final QName ATTR_MIN_BRANCH_LENGTH = new QName("MinLength");
  public static final QName ATTR_MIN_SPACE_ABOVE = new QName("MinSpaceAbove");
  public static final QName ATTR_MIN_SPACE_BELOW = new QName("MinSpaceBelow");
  
  public static final QName TAG_TEXT_LABEL = new QName(NAMESPACE_URI, "TextLabel");
  public static final QName TAG_ICON_LABEL = new QName(NAMESPACE_URI, "IconLabel");
  public static final QName ATTR_LABEL_WIDTH = new QName("Width");
  public static final QName ATTR_LABEL_HEIGHT = new QName("Height");
  public static final QName ATTR_LABEL_ABOVE = new QName("Above");
  public static final QName ATTR_LINE_NO = new QName("LineNo");
  public static final QName ATTR_LINE_POS = new QName("LinePos");
  public static final String STYLE_BOLD = "b";
  public static final String STYLE_ITALIC = "i";
  public static final String STYLE_UNDERLINE = "u";
  public static final QName ATTR_ICON = new QName("Icon");
  public static final QName ATTR_ICON_WIDTH = new QName("IconWidth");
  public static final QName ATTR_ICON_HEIGHT = new QName("IconHeight");
  public static final QName ATTR_ICON_FILLED = new QName("IconFilled");
  public static final QName ATTR_LABEL_SPACING = new QName("LabelSpacing");
  public static final QName TAG_LABEL_MARGIN = new QName(NAMESPACE_URI, "LabelMargin");

  public static final QName TAG_PIE_CHART_LABEL = new QName(NAMESPACE_URI, "PieChartLabel");
  public static final QName ATTR_SHOW_INTERNAL_LINES = new QName("InternalLines");
  public static final QName ATTR_SHOW_NULL_LINES = new QName("NullLines");
  public static final QName ATTR_SHOW_TITLE = new QName("ShowTitle");
  public static final QName ATTR_CAPTION_TYPE = new QName("CaptionType");
  public static final QName ATTR_CAPTION_LINK_TYPE = new QName("CaptionLinkType");
  public static final QName TAG_PIE_CHART_IDS = new QName(NAMESPACE_URI, "DataIds");  // Might be corrected to "DataIDs" in newer XTG-versions
  public static final QName TAG_PIE_CHART_ID = new QName(NAMESPACE_URI, "DataId");
  public static final QName ATTR_PIE_COLOR = new QName("PieColor");
  public static final QName ATTR_PIE_CAPTION = new QName("PieCaption");
  
  public static final QName TAG_HIDDEN_DATA = new QName(NAMESPACE_URI, "InvisibleData");
  
  public static final QName TAG_LEGEND = new QName(NAMESPACE_URI, "Legend");
  public static final QName ATTR_LEGEND_SPACING = new QName("LegendSpacing");
  public static final QName TAG_LEGEND_MARGIN = new QName(NAMESPACE_URI, "LegendMargin");
  public static final QName ATTR_LEGEND_POS = new QName("LegendPos");
  public static final QName ATTR_MIN_TREE_DISTANCE = new QName("MinTreeDistance");
  public static final QName ATTR_LEGEND_STYLE = new QName("LegendStyle");
  public static final QName ATTR_TEXT_ORIENTATION = new QName("Orientation");  
  public static final String ORIENT_UP = "up";
  public static final String ORIENT_DOWN = "down";
  public static final String ORIENT_HORIZONTAL = "horizontal";
  public static final String PRE_LEGEND_ANCHOR = "Anchor";  // Prefix of the legend anchor String
  public static final QName ATTR_ANCHOR_0 = new QName(PRE_LEGEND_ANCHOR + "0");
  public static final QName ATTR_ANCHOR_1 = new QName(PRE_LEGEND_ANCHOR + "1");
  public static final String STYLE_BRACE = "brace";
  public static final String STYLE_BRACKET = "bracket";
  
  public static final QName TAG_SCALE_BAR = new QName(NAMESPACE_URI, "ScaleBar");
  public static final QName ATTR_SCALE_BAR_ALIGN = new QName("Align");
  public static final String ALIGN_LEFT = "left";
  public static final String ALIGN_RIGHT = "right";
  public static final String ALIGN_TREE_WIDTH = "treeWidth";
  public static final QName ATTR_SCALE_BAR_DISTANCE = new QName("TreeDistance");
  public static final QName ATTR_SCALE_BAR_WIDTH = new QName("Width");
  public static final String BRANCH_LENGTH_UNITS = "u";
  public static final String MILLIMETERS = "mm";
  public static final QName ATTR_SCALE_BAR_HEIGHT = new QName("Height");
  public static final QName ATTR_SCALE_BAR_SMALL_INTERVAL = new QName("SmallInterval");
  public static final QName ATTR_SCALE_BAR_LONG_INTERVAL = new QName("LongInterval");
  public static final QName ATTR_SCALE_BAR_START = new QName("StartLeft");
  public static final QName ATTR_SCALE_BAR_INCREASE = new QName("Increasing");
  
  
  // Ontology constants:
  public static final String XTG_NAMESPACE_PREFIX = ReadWriteConstants.JPHYLOIO_FORMATS_NAMESPACE_PREFIX + "XTG/";	
  
  // Custom data types:
  public static final String XTG_DATA_TYPE_NAMESPACE = XTG_NAMESPACE_PREFIX + ReadWriteConstants.DATA_TYPE_NAMESPACE_FOLDER + "/";	
	public static final QName DATA_TYPE_COLOR = new QName(XTG_NAMESPACE_PREFIX, "Color");  
	public static final QName DATA_TYPE_LEGEND_STYLE = new QName(XTG_NAMESPACE_PREFIX, "LegendStyle");  
	public static final QName DATA_TYPE_TEXT_ORIENTATION = new QName(XTG_NAMESPACE_PREFIX, "TextOrientation");  
	public static final QName DATA_TYPE_SCALE_BAR_ALIGNMENT = new QName(XTG_NAMESPACE_PREFIX, "ScaleBarAlignment");  
	public static final QName DATA_TYPE_SCALE_VALUE = new QName(XTG_NAMESPACE_PREFIX, "ScaleValue");  
	public static final QName DATA_TYPE_PIE_CHART_LABEL_CAPTION_TYPE = new QName(XTG_NAMESPACE_PREFIX, "PieChartLabelCaptionType");  
	public static final QName DATA_TYPE_PIE_CHART_LABEL_CAPTION_LINK_TYPE = new QName(XTG_NAMESPACE_PREFIX, "PieChartLabelCaptionLinkType");  
  
	public static final String XTG_PREDICATE_NAMESPACE = XTG_NAMESPACE_PREFIX + ReadWriteConstants.PREDICATE_NAMESPACE_FOLDER + "/";
	
	// Margin:
	public static final QName PREDICATE_MARGIN_LEFT = new QName(XTG_PREDICATE_NAMESPACE, "MarginLeft");
	public static final QName PREDICATE_MARGIN_TOP = new QName(XTG_PREDICATE_NAMESPACE, "MarginTop");
	public static final QName PREDICATE_MARGIN_RIGHT = new QName(XTG_PREDICATE_NAMESPACE, "MarginRight");
	public static final QName PREDICATE_MARGIN_BOTTOM = new QName(XTG_PREDICATE_NAMESPACE, "MarginBottom");

	// Text formats:
	public static final QName PREDICATE_TEXT = new QName(XTG_PREDICATE_NAMESPACE, "Text");
	public static final QName PREDICATE_IS_DECIMAL = new QName(XTG_PREDICATE_NAMESPACE, "IsDecimal");
	public static final QName PREDICATE_TEXT_COLOR = new QName(XTG_PREDICATE_NAMESPACE, "TextColor");
	public static final QName PREDICATE_TEXT_HEIGHT = new QName(XTG_PREDICATE_NAMESPACE, "TextHeight");
	public static final QName PREDICATE_TEXT_STYLE = new QName(XTG_PREDICATE_NAMESPACE, "TextStyle");
	public static final QName PREDICATE_FONT_FAMILY = new QName(XTG_PREDICATE_NAMESPACE, "FontFamily");
	public static final QName PREDICATE_DECIMAL_FORMAT = new QName(XTG_PREDICATE_NAMESPACE, "DecimalFormat");
	public static final QName PREDICATE_LOCALE_LANG = new QName(XTG_PREDICATE_NAMESPACE, "LocaleLang");
	public static final QName PREDICATE_LOCALE_COUNTRY = new QName(XTG_PREDICATE_NAMESPACE, "LocaleCountry");
	public static final QName PREDICATE_LOCALE_VARIANT = new QName(XTG_PREDICATE_NAMESPACE, "LocaleVariant");
	
	// Line formats:
	public static final QName PREDICATE_LINE_COLOR = new QName(XTG_PREDICATE_NAMESPACE, "LineColor");
	public static final QName PREDICATE_LINE_WIDTH = new QName(XTG_PREDICATE_NAMESPACE, "LineWidth");

	// Global formats:
	public static final QName PREDICATE_GLOBAL_FORMATS = new QName(XTG_PREDICATE_NAMESPACE, "GlobalFormats");
	public static final QName PREDICATE_GLOBAL_FORMATS_BG_COLOR = new QName(XTG_PREDICATE_NAMESPACE, "GlobalFormats" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "BgColor");
	public static final QName PREDICATE_GLOBAL_FORMATS_BRANCH_LENGTH_SCALE = new QName(XTG_PREDICATE_NAMESPACE, "GlobalFormats" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "BranchLengthScale");
	public static final QName PREDICATE_GLOBAL_FORMATS_SHOW_SCALE_BAR = new QName(XTG_PREDICATE_NAMESPACE, "GlobalFormats" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "ShowScaleBar");
	public static final QName PREDICATE_GLOBAL_FORMATS_SHOW_ROOTED = new QName(XTG_PREDICATE_NAMESPACE, "GlobalFormats" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "ShowRooted");
	public static final QName PREDICATE_GLOBAL_FORMATS_ALIGN_TO_SUBTREE = new QName(XTG_PREDICATE_NAMESPACE, "GlobalFormats" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "AlignToSubtree");
	public static final QName PREDICATE_GLOBAL_FORMATS_POSITION_LABELS_TO_LEFT = new QName(XTG_PREDICATE_NAMESPACE, "GlobalFormats" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "LabelsLeft");
	
	public static final QName PREDICATE_DOCUMENT_MARGIN = new QName(XTG_PREDICATE_NAMESPACE, "DocMargin");
	
	// General:
	public static final QName PREDICATE_WIDTH = new QName(XTG_PREDICATE_NAMESPACE, "Width");
	public static final QName PREDICATE_HEIGHT = new QName(XTG_PREDICATE_NAMESPACE, "Height");

	// Node:
	public static final QName PREDICATE_NODE_UNIQUE_NAME = new QName(XTG_PREDICATE_NAMESPACE, "Node" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "UniqueName");
	public static final QName PREDICATE_EDGE_RADIUS = new QName(XTG_PREDICATE_NAMESPACE, "EdgeRadius");
	public static final QName PREDICATE_LEAF_MARGIN = new QName(XTG_PREDICATE_NAMESPACE, "LeafMargin");

	// Branch:
	public static final QName PREDICATE_BRANCH_CONSTANT_WIDTH = new QName(XTG_PREDICATE_NAMESPACE, "Branch" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "ConstantWidth");
	public static final QName PREDICATE_BRANCH_MIN_LENGTH = new QName(XTG_PREDICATE_NAMESPACE, "Branch" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "MinLength");
	public static final QName PREDICATE_BRANCH_MIN_SPACE_ABOVE = new QName(XTG_PREDICATE_NAMESPACE, "Branch" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "MinSpaceAbove");
	public static final QName PREDICATE_BRANCH_MIN_SPACE_BELOW = new QName(XTG_PREDICATE_NAMESPACE, "Branch" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "MinSpaceBelow");
	
	// Invisible data:
	public static final QName PREDICATE_INVISIBLE_DATA = new QName(XTG_PREDICATE_NAMESPACE, "InvisibleData");
	public static final QName PREDICATE_COLUMN_ID = new QName(XTG_PREDICATE_NAMESPACE, "ColumnID");
	
	// Label:
	public static final QName PREDICATE_LABEL_ABOVE = new QName(XTG_PREDICATE_NAMESPACE, "Label" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Above");
	public static final QName PREDICATE_LABEL_LINE_NO = new QName(XTG_PREDICATE_NAMESPACE, "Label" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "LineNo");
	public static final QName PREDICATE_LABEL_LINE_POS = new QName(XTG_PREDICATE_NAMESPACE, "Label" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "LinePos");
	
	public static final QName PREDICATE_TEXT_LABEL = new QName(XTG_PREDICATE_NAMESPACE, "TextLabel");
	
	public static final QName PREDICATE_ICON_LABEL = new QName(XTG_PREDICATE_NAMESPACE, "IconLabel");
	public static final QName PREDICATE_ICON_LABEL_ICON = new QName(XTG_PREDICATE_NAMESPACE, "IconLabel" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Icon");
	public static final QName PREDICATE_ICON_LABEL_ICON_FILLED = new QName(XTG_PREDICATE_NAMESPACE, "IconLabel" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "IconFilled");
	
	public static final QName PREDICATE_PIE_CHART_LABEL = new QName(XTG_PREDICATE_NAMESPACE, "PieChartLabel");
	public static final QName PREDICATE_PIE_CHART_LABEL_INTERNAL_LINES = new QName(XTG_PREDICATE_NAMESPACE, "PieChartLabel" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "InternalLines");
	public static final QName PREDICATE_PIE_CHART_LABEL_NULL_LINES = new QName(XTG_PREDICATE_NAMESPACE, "PieChartLabel" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "NullLines");
  public static final QName PREDICATE_PIE_CHART_LABEL_SHOW_TITLE = new QName(XTG_PREDICATE_NAMESPACE, "PieChartLabel" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "ShowTitle");
  public static final QName PREDICATE_PIE_CHART_LABEL_CAPTION_TYPE = new QName(XTG_PREDICATE_NAMESPACE, "PieChartLabel" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "CaptionType");
  public static final QName PREDICATE_PIE_CHART_LABEL_CAPTION_LINK_TYPE = new QName(XTG_PREDICATE_NAMESPACE, "PieChartLabel" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "CaptionLinkType");
	
	public static final QName PREDICATE_DATA_IDS = new QName(XTG_PREDICATE_NAMESPACE, "DataIDs");
	public static final QName PREDICATE_DATA_ID = new QName(XTG_PREDICATE_NAMESPACE, "DataID");
	public static final QName PREDICATE_PIE_COLOR = new QName(XTG_PREDICATE_NAMESPACE, "DataID" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "PieColor");
  public static final QName PREDICATE_PIE_CAPTION = new QName(XTG_PREDICATE_NAMESPACE, "DataID" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "PieCaption");
	public static final QName PREDICATE_DATA_ID_VALUE = new QName(XTG_PREDICATE_NAMESPACE, "DataID" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	
	public static final QName PREDICATE_LABEL_MARGIN = new QName(XTG_PREDICATE_NAMESPACE, "LabelMargin");
	
	public static final QName PREDICATE_SCALE_BAR = new QName(XTG_PREDICATE_NAMESPACE, "ScaleBar");
	public static final QName PREDICATE_SCALE_BAR_ALIGN = new QName(XTG_PREDICATE_NAMESPACE, "ScaleBar" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Align");
	public static final QName PREDICATE_SCALE_BAR_TREE_DISTANCE = new QName(XTG_PREDICATE_NAMESPACE, "ScaleBar" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "TreeDistance");
	public static final QName PREDICATE_SCALE_BAR_SMALL_INTERVAL = new QName(XTG_PREDICATE_NAMESPACE, "ScaleBar" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "SmallInterval");
	public static final QName PREDICATE_SCALE_BAR_LONG_INTERVAL = new QName(XTG_PREDICATE_NAMESPACE, "ScaleBar" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "LongInterval");
	public static final QName PREDICATE_SCALE_BAR_START_LEFT = new QName(XTG_PREDICATE_NAMESPACE, "ScaleBar" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "StartLeft");
	public static final QName PREDICATE_SCALE_BAR_INCREASING = new QName(XTG_PREDICATE_NAMESPACE, "ScaleBar" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Increasing");
	
	public static final QName PREDICATE_LEGEND = new QName(XTG_PREDICATE_NAMESPACE, "Legend");
	public static final QName PREDICATE_LEGEND_ANCHOR_0 = new QName(XTG_PREDICATE_NAMESPACE, "Legend" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Anchor0");
	public static final QName PREDICATE_LEGEND_ANCHOR_1 = new QName(XTG_PREDICATE_NAMESPACE, "Legend" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Anchor1");
	public static final QName PREDICATE_LEGEND_POSITION = new QName(XTG_PREDICATE_NAMESPACE, "Legend" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "LegendPosition");
	public static final QName PREDICATE_LEGEND_MIN_TREE_DISTANCE = new QName(XTG_PREDICATE_NAMESPACE, "Legend" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "MinTreeDistance");
	public static final QName PREDICATE_LEGEND_SPACING = new QName(XTG_PREDICATE_NAMESPACE, "Legend" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "LegendSpacing");
	public static final QName PREDICATE_LEGEND_STYLE = new QName(XTG_PREDICATE_NAMESPACE, "Legend" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "LegendStyle");
	public static final QName PREDICATE_LEGEND_ORIENTATION = new QName(XTG_PREDICATE_NAMESPACE, "Legend" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Orientation");
	public static final QName PREDICATE_LEGEND_MARGIN = new QName(XTG_PREDICATE_NAMESPACE, "LegendMargin");
	
	public static final QName PREDICATE_NODE_BRANCH_DATA_ADAPTERS = new QName(XTG_PREDICATE_NAMESPACE, "NodeBranchDataAdapter");
	public static final QName PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER = new QName(XTG_PREDICATE_NAMESPACE, "NodeBranchDataAdapter" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Adapter");
	public static final QName PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER_NAME = new QName(XTG_PREDICATE_NAMESPACE, "NodeBranchDataAdapter" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Adapter" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Name");
	public static final QName PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER_ID = new QName(XTG_PREDICATE_NAMESPACE, "NodeBranchDataAdapter" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Adapter" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "ID");
	public static final QName PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER_PURPOSE = new QName(XTG_PREDICATE_NAMESPACE, "NodeBranchDataAdapter" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Adapter" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Purpose");
}