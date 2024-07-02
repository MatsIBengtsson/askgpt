<!-- This file has been modified by Mats Bengtsson.
Original file is part of the Nerdy Things AskGPT project.
-->
# ChatGPT helper for IntelliJ IDEA

This plugin makes ChatGPT requests for you based on your files. It then pastes the answers into a separate tab in the editor.
This simplifies the usage, staying in the IDE for all steps. For example, suggested code changes can be copied to the clipboard,
and then compared to current code (using the IDE function compare to clipboard).

Similarly, you can select what information to send to ChatGPT from the IDE when asking the question. Nothing more than your own question, or a selected text in
the code window you stood when requesting the GPT answer, or the whole file currently open in the editor, or any number of files,
selectable from the IDE using the file selection dialog.

The ability to send multiple files is essential and affects the answers. For example, if you ask AskGPT to detect possible issues, it will suggest
different tests to add if it sees or do not see the tests existing in referred classes and functions.

THere is a number of predefined prompts. All prompts can be modified by you. The generic AskGPT prompt is always changed to become the last prompt you used
when using that menue selection. The other prompts can be altered, yet always reverts to the default next time you run them, so you do not have to reinvent them.

## How It Works

*  Go to the OpenAI site and create API key: <a href="https://platform.openai.com/api-keys">https://platform.openai.com/api-keys</a>.
* In IDE open a Tools menu and press <b>AskGpt Settings</b>. Paste the ChatGPT API key into the <b>GPT Token</b> field (the first one).

To call the plugin you need to launch it from tools -> Generate... This can also can be done by pressing *Alt+Insert* shortcut in the code.
A context-sensitive menu will appear, allowing you to define your task for GPT in detail.

The plugin will then send the request and selected material to ChatGPT. The response is provided direct in your IDE, right where you need them.

## Key Features
* **Custom Asks**: Query ChatGPT directly to generate custom code snippets, algorithms, or tackle programming challenges right within your IDE.
* **Refactor Code**: Request refactoring suggestions, for a selection in the editor, a whole file, or multiple files at once.
* **Write Tests**: Automatically generate test cases for your code. Simply specify the target, and let ChatGPT create comprehensive tests to ensure reliability.
* **Find Issues**: Describe your code to ChatGPT, and it will help identify potential flaws in your code, suggest debugging steps, or offer direct solutions.
* **Create Docs**: Generate documentation effortlessly. ChatGPT can document your functions, classes, and modules, making your codebase easier to navigate and maintain.

## Old Video Description
[![Watch the video](/images/thumbnail.png)](https://youtu.be/4i7ql-CZRkw)

## About
![Screenshot1](/images/animation.webp)

![Screenshot3](/images/image2.png)