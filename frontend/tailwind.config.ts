import type { Config } from "tailwindcss";

export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: "#54457F",      
        secondary: "#9BA2FF",  
        accent: "#E85D75",     
        black: "#000000",
        white: "#FFFFFF",
      },
    },
  },
  plugins: [],
} satisfies Config;