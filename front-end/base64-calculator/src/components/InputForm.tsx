import * as React from "react";

/** Input Form's state type. */
interface InputFormState {
    /** Checkbox for whether to add padding characters. */
    paddingToggle: boolean;
    /** Input to be encoded. */
    textInput: string;
    /** Encoded output. */
    textOutput: string;
}

/** Main input form for the app. User types in text to be base64 encoded. */
export default class InputForm extends React.Component<undefined, InputFormState> {
    private base64table = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    /** The state of the object consists of the padding character toggle,
     * the input text, and the output text.
     */
    constructor() {
        super();
        this.state = {paddingToggle: true, textInput: "", textOutput: ""};
        this.handleTextChange = this.handleTextChange.bind(this);
        this.handleCheckboxChange = this.handleCheckboxChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    public render() {
        return (
            <div>
                <form onSubmit = {this.handleSubmit}>
                    <textarea
                        value = {this.state.textInput}
                        onChange = {this.handleTextChange}
                    />
                    <input
                        type = "submit"
                        value = "Submit"
                    />
                    <input
                        type = "checkbox"
                        checked = {this.state.paddingToggle}
                        onChange = {this.handleCheckboxChange}
                    /> Include Padding
                    <br/>
                    <output>
                        {this.state.textOutput}
                    </output>
                  </form>
            </div>
        );
    }

    private handleTextChange(event: React.FormEvent<HTMLTextAreaElement>) {
        this.setState(
            {
                textInput: event.currentTarget.value,
            },
        );
    }

    private handleCheckboxChange(event: React.FormEvent<HTMLInputElement>) {
        this.setState(
            {
                paddingToggle: event.currentTarget.checked,
            },
        );
    }

    private handleSubmit(event: React.FormEvent<HTMLFormElement>) {
        this.setState(
            {
                textOutput: this.base64encode(this.state.textInput, this.state.paddingToggle),
            },
        );
        // This keeps the form from Posting and refreshing the page.
        event.preventDefault();
    }

    private base64encode(input: string, padding: boolean): string {
        let output = "";
        for (let i = 0; i < input.length; i += 3) {
            output += this.base64table.charAt((input.charCodeAt(i) & 0xFC) >> 2);
            if (i + 1 >= input.length) {
                output += this.base64table.charAt((input.charCodeAt(i) & 0x3) << 6);
                if (padding) {
                    output += "==";
                }
                return output;
            }

            output += this.base64table.charAt(((input.charCodeAt(i) & 0x3) << 4) |
                                              ((input.charCodeAt(i + 1) & 0xF0) >> 4));
            if (i + 2 >= input.length) {
                output += this.base64table.charAt((input.charCodeAt(i + 1) & 0xF) << 2);
                if (padding) {
                    output += "=";
                }
                return output;
            }

            output += this.base64table.charAt(((input.charCodeAt(i + 1) & 0xF) << 2) |
                                              ((input.charCodeAt(i + 2) & 0xC0) >> 6));

            output += this.base64table.charAt((input.charCodeAt(i + 2) & 0x3F));
        }

        return output;
    }
}
