<!-- This file has been modified by Mats Bengtsson.
Original file is part of the Nerdy Things AskGPT project.
-->
# ChatGPT helper for IntelliJ IDEA

This plugin, which is an extension of the Nerdy Things AskGPT, helps you make ChatGPT requests from the IDE.

It allows you to include your IDEA/PyCharm files, editor content, selected text, on top of your typed queries.

It pastes the ChatGPT answer into a separate tab in your editor, which simplifies the usage. You can stay in your
IDE for all steps. For example, code changes suggested by ChatGPT can be copied to the clipboard,
and then compared to current code (using the IDE function compare to clipboard).

You can easily select what information to send to ChatGPT from the IDE when asking the question.
It can be nothing more than your typed question, or a selected text in the code window from which you made the
request for a ChatGPT response, or a whole file currently open in the editor, or any number of files,
selectable from the IDE using the file selection dialog.

The ability to send multiple files is essential and affects the answers. For example, if you ask ChatGPT to detect
possible issues, it will suggest different tests to add depending on what code it sees is already existing in
referred classes and functions. Similar with refactoring, troubleshooting, ...

There is a number of predefined prompts. All prompt texts can be modified by you. The generic AskGPT prompt is
updated to start from the last prompt you used whenever you used that menu selection. This is very helpful, 
since the API ChatGPT has no memory of previous questions. You may then want to make resends where you have added
some further comments or files, to ensure that the next response avoids routes you already know are wrong.

The other prompts can be altered, yet will always revert to their default next time you run them. Thus you do not 
have to reinvent their starting points. Their defaults can be changed by you, using the AskGPT Settings menu.

## How It Works

* Go to the OpenAI site and create API key: <a href="https://platform.openai.com/api-keys">https://platform.openai.com/api-keys</a>.
* In IDE open a Tools menu and press <b>AskGpt Settings</b>. Paste the ChatGPT API key into the <b>GPT Token</b> field (the first one).

To call the plugin you need to launch it from either the tools menu where "AskGPT MIB add" contains different AskGPT menus,
or from the tools -> Generate... menu. That menu can also be reached by right-clicking, placing the AskGPT submenus just
a right click away.

The generate menu is default invoked using shortcut *Alt+Insert*. The AskGPT MIB Add settings contain an option to
define a separate shortcut key to pop up the AskGPT MIB Add submenues close to your cursor when you are in the editor.

When you select the proper submenu, you get to define the request to send to ChatGPT in detail, including what information
to supply to ChatGPT beside the question text.

The plugin will then send the request and selected information to ChatGPT. The response is provided direct in your IDE,
in a separate tab in the editor, making it easy to use and refine.

## Key Features
* **Settings**:Define default prompts and possible shortcut for using the below options from the IDE.
* **Custom Asks**: Query ChatGPT directly to generate custom code snippets, algorithms, or tackle programming challenges right within your IDE.
* **Refactor Code**: Request refactoring suggestions, for a selection in the editor, a whole file, or multiple files at once.
* **Write Tests**: Automatically generate test cases for your code. Simply specify the target, and let ChatGPT create comprehensive tests to ensure reliability.
* **Find Issues**: Describe your code to ChatGPT, and it will help identify potential flaws in your code, suggest debugging steps, or offer direct solutions.
* **Create Docs**: Generate documentation effortlessly. ChatGPT can document your functions, classes, and modules, making your codebase easier to navigate and maintain.

## Old Video Description
[![Watch the video](/images/thumbnailadd.png)](https://youtu.be/4i7ql-CZRkw)

## About
![Screenshot1](/images/animation.webp)

![Screenshot3](/images/image2.png)