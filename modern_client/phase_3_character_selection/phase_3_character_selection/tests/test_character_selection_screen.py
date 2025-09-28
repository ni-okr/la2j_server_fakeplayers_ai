
# test_character_selection_screen.py
import unittest
import os
import json

class TestCharacterSelectionScreen(unittest.TestCase):

    def setUp(self):
        self.analysis_file = os.path.join(os.path.dirname(__file__), "..", "analysis", "character_selection_analysis.json")
        self.implementation_dir = os.path.join(os.path.dirname(__file__), "..", "implementation")
        self.character_screen_h = os.path.join(self.implementation_dir, "CharacterSelectionScreen.h")
        self.character_screen_cpp = os.path.join(self.implementation_dir, "CharacterSelectionScreen.cpp")
        self.wbp_character_screen_json = os.path.join(self.implementation_dir, "WBP_CharacterSelectionScreen.json")

    def test_analysis_file_exists(self):
        self.assertTrue(os.path.exists(self.analysis_file), "Файл анализа экрана выбора персонажей не найден.")

    def test_cpp_files_exist(self):
        self.assertTrue(os.path.exists(self.character_screen_h), "CharacterSelectionScreen.h не найден.")
        self.assertTrue(os.path.exists(self.character_screen_cpp), "CharacterSelectionScreen.cpp не найден.")

    def test_blueprint_template_exists(self):
        self.assertTrue(os.path.exists(self.wbp_character_screen_json), "WBP_CharacterSelectionScreen.json не найден.")

    def test_blueprint_template_content(self):
        with open(self.wbp_character_screen_json, "r", encoding="utf-8") as f:
            content = json.load(f)
        self.assertIn("CharacterListPanel", [comp["Name"] for comp in content["Components"]])
        self.assertIn("CreateCharacterButton", [comp["Name"] for comp in content["Components"]])
        self.assertEqual(content["ParentClass"], "CharacterSelectionScreen")

    def test_cpp_header_content(self):
        with open(self.character_screen_h, "r", encoding="utf-8") as f:
            content = f.read()
        self.assertIn("class UCharacterSelectionScreen : public UUserWidget", content)
        self.assertIn("UScrollBox* CharacterListPanel", content)
        self.assertIn("void OnCreateCharacterButtonClicked();", content)

    def test_cpp_source_content(self):
        with open(self.character_screen_cpp, "r", encoding="utf-8") as f:
            content = f.read()
        self.assertIn("UCharacterSelectionScreen::OnCreateCharacterButtonClicked()", content)
        self.assertIn("Super::NativeConstruct();", content)

    def test_analysis_content(self):
        with open(self.analysis_file, "r", encoding="utf-8") as f:
            content = json.load(f)
        self.assertEqual(content["screen_name"], "CharacterSelectionScreen")
        self.assertIn("CharacterListPanel", [elem["name"] for elem in content["elements"]])
        self.assertIn("CreateCharacterButton", [elem["name"] for elem in content["elements"]])

if __name__ == '__main__':
    unittest.main()
