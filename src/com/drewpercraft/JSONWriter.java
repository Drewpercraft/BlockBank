package com.drewpercraft;

import java.io.StringWriter;

public class JSONWriter extends StringWriter
{
        final static String indentstring = "  "; //define as you wish
        final static String spaceaftercolon = " "; //use "" if you don't want space after colon

        private int indentlevel = 0;

        @Override
        public void write(int c)
        {
                char ch = (char) c;
                if (ch == '[' || ch == '{')
                {
                        super.write(c);
                        super.write(System.lineSeparator());
                        indentlevel++;
                        writeIndentation();
                } else if (ch == ',')
                {
                        super.write(c);
                        super.write(System.lineSeparator());
                        writeIndentation();
                } else if (ch == ']' || ch == '}')
                {
                        super.write(System.lineSeparator());
                        indentlevel--;
                        writeIndentation();
                        super.write(c);
                } else if (ch == ':')
                {
                        super.write(c);
                        super.write(spaceaftercolon);
                } else
                {
                        super.write(c);
                }

        }

        @Override
        public void write(String text)
        {
        	for(int i=0; i < text.length(); i++) {
        		write(text.charAt(i));
        	}
        }
        private void writeIndentation()
        {
                for (int i = 0; i < indentlevel; i++)
                {
                        super.write(indentstring);
                }
        }
}