// Small helpers used by the clinic pages.

// Opens the browser print dialog for the receipt page.
function printReceipt() {
    window.print();
}

// Used by the delete/deactivate buttons so the user confirms first.
function confirmAction(message) {
    return window.confirm(message);
}
