package org.elmlang.intellijplugin.features.syntaxHighlighting;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.elmlang.intellijplugin.ElmIcons;
import org.elmlang.intellijplugin.features.syntaxHighlighting.ElmSyntaxHighlighter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static org.elmlang.intellijplugin.features.syntaxHighlighting.ElmSyntaxHighlighter.*;

public class ElmColorSettingsPage implements ColorSettingsPage {

    @NonNls
    private static final Map<String, TextAttributesKey> TAG_HIGHLIGHTING_MAP = new HashMap<String, TextAttributesKey>();
    static {
        TAG_HIGHLIGHTING_MAP.put("sig_left", ELM_TYPE_ANNOTATION_NAME);
        TAG_HIGHLIGHTING_MAP.put("sig_right", ELM_TYPE_ANNOTATION_SIGNATURE_TYPES);
        TAG_HIGHLIGHTING_MAP.put("type", ELM_TYPE);
        TAG_HIGHLIGHTING_MAP.put("func_decl", ELM_DEFINITION_NAME);
    }

    private static final AttributesDescriptor[] ATTRIBS = {
            new AttributesDescriptor("Keyword", ELM_KEYWORD),
            new AttributesDescriptor("Number", ELM_NUMBER),
            new AttributesDescriptor("String", ELM_STRING),
            new AttributesDescriptor("Operator", ELM_OPERATOR),
            new AttributesDescriptor("Type", ELM_TYPE),
            new AttributesDescriptor("Definition Name", ELM_DEFINITION_NAME),
            new AttributesDescriptor("Type Annotation//Name", ELM_TYPE_ANNOTATION_NAME),
            new AttributesDescriptor("Type Annotation//Signature", ELM_TYPE_ANNOTATION_SIGNATURE_TYPES),
            new AttributesDescriptor("Punctuation//Arrows", ELM_ARROW),
            new AttributesDescriptor("Punctuation//Parentheses", ELM_PARENTHESIS),
            new AttributesDescriptor("Punctuation//Braces", ELM_BRACES),
            new AttributesDescriptor("Punctuation//Brackets", ELM_BRACKETS),
            new AttributesDescriptor("Punctuation//Comma", ELM_COMMA),
            new AttributesDescriptor("Punctuation//Dot", ELM_DOT),
            new AttributesDescriptor("Punctuation//Equals", ELM_EQ),
            new AttributesDescriptor("Punctuation//Pipe", ELM_PIPE),
    };

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTRIBS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Elm";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ElmIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new ElmSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "module Todo exposing (..)\n" +
                "\n" +
                "import Html exposing (div, h1, ul, li, text)\n" +
                "\n" +
                "-- a single line comment\n" +
                "\n" +
                "type alias <type>Model</type> =\n" +
                "    { page : <type>Int</type>\n" +
                "    , title : <type>String</type>\n" +
                "    , stepper : <type>Int</type> -> <type>Int</type>\n" +
                "    }\n" +
                "\n" +
                "type <type>Msg</type>\n" +
                "    = ModeA\n" +
                "    | ModeB <type>Int</type>\n" +
                "\n" +
                "<sig_left>update</sig_left> : <sig_right>Msg</sig_right> -> <sig_right>Model</sig_right> -> ( <sig_right>Model</sig_right>, <sig_right>Cmd Msg</sig_right> )\n" +
                "<func_decl>update</func_decl> msg model =\n" +
                "  case msg of\n" +
                "    ModeA ->\n" +
                "      { model\n" +
                "        | page = 0\n" +
                "        , title = \"Mode A\"\n" +
                "        , stepper = (\\k -> k + 1)\n" +
                "      }\n" +
                "        ! []\n" +
                "\n" +
                "<sig_left>view</sig_left> : <sig_right>Model</sig_right> -> <sig_right>Html.Html Msg</sig_right>\n" +
                "<func_decl>view</func_decl> model =\n" +
                "  let\n" +
                "    itemify label =\n" +
                "      li [] [ text label ]\n" +
                "  in\n" +
                "    div []\n" +
                "      [ h1 [] [ text \"Chapter One\" ]\n" +
                "      , ul []\n" +
                "          (List.map itemify model.items)\n" +
                "      ]\n";
}

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return TAG_HIGHLIGHTING_MAP;
    }
}
