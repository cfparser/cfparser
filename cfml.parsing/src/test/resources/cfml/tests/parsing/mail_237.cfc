cfmail(
    to=toAddress,
    from=adminEmail,
    replyto=adminEmail,
    failto=adminEmail,
    subject="Test email",
    type="html"
) {
     writeOutput(mailBody);
};