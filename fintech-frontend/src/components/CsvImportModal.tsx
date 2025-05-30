import { useState } from 'react';
import { useImportTransactions } from '../hooks/useTransactions';
import { useAccounts } from '../hooks/useAccounts';
import { X, Upload, FileText, CheckCircle, AlertCircle, Download } from 'lucide-react';
import { showSuccessToast, showErrorToast } from '../lib/errorHandler';

interface CsvImportModalProps {
  isOpen: boolean;
  onClose: () => void;
  accountId?: string;
}

interface ImportResult {
  totalRows: number;
  successfulImports: number;
  failedImports: number;
  errors: string[];
  importedTransactions: any[];
}

export const CsvImportModal = ({ isOpen, onClose, accountId }: CsvImportModalProps) => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [selectedAccountId, setSelectedAccountId] = useState(accountId || '');
  const [importResult, setImportResult] = useState<ImportResult | null>(null);
  const [isImporting, setIsImporting] = useState(false);

  const { data: accounts } = useAccounts();
  const importTransactions = useImportTransactions();

  const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file && file.type === 'text/csv') {
      setSelectedFile(file);
      setImportResult(null);
    } else {
      showErrorToast('Please select a valid CSV file');
    }
  };

  const handleImport = async () => {
    if (!selectedFile || !selectedAccountId) {
      showErrorToast('Please select a file and account');
      return;
    }

    setIsImporting(true);
    try {
      const result = await importTransactions.mutateAsync({
        accountId: selectedAccountId,
        file: selectedFile,
      });
      setImportResult(result);
      showSuccessToast(`Import completed: ${result.successfulImports} transactions imported`);
    } catch (error) {
      showErrorToast('Import failed. Please try again.');
    } finally {
      setIsImporting(false);
    }
  };

  const handleClose = () => {
    setSelectedFile(null);
    setImportResult(null);
    onClose();
  };

  const downloadSampleCsv = () => {
    const csvContent = `postedAt,amount,description,merchant,category
2024-01-15T10:30:00Z,-25.50,Starbucks Coffee,Starbucks,Dining
2024-01-16T14:20:00Z,-89.99,Grocery Store,Whole Foods,Groceries
2024-01-17T09:15:00Z,2500.00,Salary Deposit,Company Inc,Salary
2024-01-18T16:45:00Z,-1200.00,Rent Payment,Landlord,Rent
2024-01-19T11:30:00Z,-45.67,Gas Station,Shell,Transportation`;

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'sample_transactions.csv';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      <div className="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" onClick={handleClose} />
        
        <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
          <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg leading-6 font-medium text-gray-900">
                Import Transactions from CSV
              </h3>
              <button
                onClick={handleClose}
                className="text-gray-400 hover:text-gray-600"
              >
                <X className="h-6 w-6" />
              </button>
            </div>

            {!importResult ? (
              <div className="space-y-4">
                {/* Account Selection */}
                <div>
                  <label htmlFor="account" className="block text-sm font-medium text-gray-700">
                    Select Account
                  </label>
                  <select
                    id="account"
                    value={selectedAccountId}
                    onChange={(e) => setSelectedAccountId(e.target.value)}
                    className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  >
                    <option value="">Choose an account</option>
                    {accounts?.map((account) => (
                      <option key={account.id} value={account.id}>
                        {account.name} ({account.accountType})
                      </option>
                    ))}
                  </select>
                </div>

                {/* File Upload */}
                <div>
                  <label htmlFor="file" className="block text-sm font-medium text-gray-700">
                    CSV File
                  </label>
                  <div className="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-gray-300 border-dashed rounded-md hover:border-gray-400 transition-colors">
                    <div className="space-y-1 text-center">
                      <Upload className="mx-auto h-12 w-12 text-gray-400" />
                      <div className="flex text-sm text-gray-600">
                        <label
                          htmlFor="file-upload"
                          className="relative cursor-pointer bg-white rounded-md font-medium text-indigo-600 hover:text-indigo-500 focus-within:outline-none focus-within:ring-2 focus-within:ring-offset-2 focus-within:ring-indigo-500"
                        >
                          <span>Upload a file</span>
                          <input
                            id="file-upload"
                            name="file-upload"
                            type="file"
                            className="sr-only"
                            accept=".csv"
                            onChange={handleFileSelect}
                          />
                        </label>
                        <p className="pl-1">or drag and drop</p>
                      </div>
                      <p className="text-xs text-gray-500">CSV files only</p>
                    </div>
                  </div>
                  {selectedFile && (
                    <div className="mt-2 flex items-center text-sm text-gray-600">
                      <FileText className="h-4 w-4 mr-2" />
                      {selectedFile.name}
                    </div>
                  )}
                </div>

                {/* Sample CSV Download */}
                <div className="bg-gray-50 rounded-md p-3">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm font-medium text-gray-700">Need a sample CSV?</p>
                      <p className="text-xs text-gray-500">Download our template to get started</p>
                    </div>
                    <button
                      onClick={downloadSampleCsv}
                      className="inline-flex items-center px-3 py-1 border border-gray-300 shadow-sm text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50"
                    >
                      <Download className="h-3 w-3 mr-1" />
                      Download Sample
                    </button>
                  </div>
                </div>
              </div>
            ) : (
              /* Import Results */
              <div className="space-y-4">
                <div className="bg-green-50 border border-green-200 rounded-md p-4">
                  <div className="flex">
                    <CheckCircle className="h-5 w-5 text-green-400" />
                    <div className="ml-3">
                      <h3 className="text-sm font-medium text-green-800">
                        Import Completed Successfully
                      </h3>
                      <div className="mt-2 text-sm text-green-700">
                        <p>Total rows processed: {importResult.totalRows}</p>
                        <p>Successfully imported: {importResult.successfulImports}</p>
                        <p>Failed imports: {importResult.failedImports}</p>
                      </div>
                    </div>
                  </div>
                </div>

                {importResult.errors.length > 0 && (
                  <div className="bg-red-50 border border-red-200 rounded-md p-4">
                    <div className="flex">
                      <AlertCircle className="h-5 w-5 text-red-400" />
                      <div className="ml-3">
                        <h3 className="text-sm font-medium text-red-800">
                          Import Errors
                        </h3>
                        <div className="mt-2 text-sm text-red-700">
                          <ul className="list-disc list-inside space-y-1">
                            {importResult.errors.slice(0, 5).map((error, index) => (
                              <li key={index}>{error}</li>
                            ))}
                            {importResult.errors.length > 5 && (
                              <li>... and {importResult.errors.length - 5} more errors</li>
                            )}
                          </ul>
                        </div>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>

          <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
            {!importResult ? (
              <>
                <button
                  type="button"
                  onClick={handleImport}
                  disabled={!selectedFile || !selectedAccountId || isImporting}
                  className="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3 sm:w-auto sm:text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {isImporting ? 'Importing...' : 'Import Transactions'}
                </button>
                <button
                  type="button"
                  onClick={handleClose}
                  className="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
                >
                  Cancel
                </button>
              </>
            ) : (
              <button
                type="button"
                onClick={handleClose}
                className="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:w-auto sm:text-sm"
              >
                Close
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
