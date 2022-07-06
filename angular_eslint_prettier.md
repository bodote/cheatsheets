## Install eslint und prettier

```bash
ng add @angular-eslint/schematics
npm install --save-dev eslint-plugin-prettier
npm install --save-dev --save-exact prettier
npm install --save-dev eslint-config-prettier
npm install -g eslint
```

add ` "plugin:prettier/recommended"`to .eslintrc.json in `"overrides": [.."extends": [`:

```json
{
  "root": true,
  "ignorePatterns": [
    "projects/**/*"
  ],
  "overrides": [
    {
      "files": [
        "*.ts"
      ],
      "parserOptions": {
        "project": [
          "tsconfig.json"
        ],
        "createDefaultProgram": true
      },
      "extends": [
        "plugin:@angular-eslint/recommended",
        "plugin:@angular-eslint/template/process-inline-templates",
        "plugin:prettier/recommended" //this line is important!
      ]
```

see : https://javascript.plainenglish.io/how-to-configure-eslint-and-prettier-on-angular-application-87dbd767369c
for more details


## VisualCode eslint und prettier
* ESLint-Microsoft plugin
* Prettier Eslint plugin
* Disable normal Pettier Code Formatter, Shift-Command-P -> "Format Document with..."->"Configure Default Formatter"->"Eslint-Prettier"
* VSCode restart ESLint server with CommandPalette: `ESLint: restart ESLint server`

## command line 
run on all files: 
`npx eslint --fix "src/**/*.ts"`