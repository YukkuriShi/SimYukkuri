package src.system;


import java.lang.RuntimeException;

public class LanguageException extends RuntimeException
{
    static final long serialVersionUID = 1L;
    LanguageException() { super("Unknown language option."); }
};