/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx,ts,tsx}"
  ],
  theme: {
    extend: {
      colors: {
        primary: "#1e3a5f",
        "primary-light": "#2d4a6f",
        accent: "#ff6b6b",
        "accent-light": "#ff8787"
      }
    }
  },
  plugins: []
};
