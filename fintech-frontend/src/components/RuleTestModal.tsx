import { useState } from 'react';
import { useTestRule } from '../hooks/useRules';
import { X, Play, CheckCircle, XCircle, AlertCircle } from 'lucide-react';

interface RuleTestModalProps {
  isOpen: boolean;
  onClose: () => void;
  initialPattern?: string;
  initialTestText?: string;
}

export const RuleTestModal = ({
  isOpen,
  onClose,
  initialPattern = '',
  initialTestText = '',
}: RuleTestModalProps) => {
  const [pattern, setPattern] = useState(initialPattern);
  const [testText, setTestText] = useState(initialTestText);
  const [testResult, setTestResult] = useState<{ matches: boolean; matchedText?: string } | null>(null);
  const [isValidPattern, setIsValidPattern] = useState(true);
  const [patternError, setPatternError] = useState('');

  const testRule = useTestRule();

  const validatePattern = (pattern: string) => {
    if (!pattern.trim()) {
      setIsValidPattern(true);
      setPatternError('');
      return true;
    }

    try {
      new RegExp(pattern);
      setIsValidPattern(true);
      setPatternError('');
      return true;
    } catch (error) {
      setIsValidPattern(false);
      setPatternError('Invalid regular expression pattern');
      return false;
    }
  };

  const handlePatternChange = (value: string) => {
    setPattern(value);
    validatePattern(value);
    setTestResult(null);
  };

  const handleTest = async () => {
    if (!pattern.trim() || !testText.trim() || !isValidPattern) {
      return;
    }

    try {
      const result = await testRule.mutateAsync({ pattern, testText });
      setTestResult(result);
    } catch (error) {
      console.error('Failed to test rule:', error);
    }
  };

  const handleClose = () => {
    setPattern(initialPattern);
    setTestText(initialTestText);
    setTestResult(null);
    setIsValidPattern(true);
    setPatternError('');
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      <div className="flex min-h-screen items-end justify-center px-4 pt-4 pb-20 text-center sm:block sm:p-0">
        <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" onClick={handleClose} />

        <span className="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">
          &#8203;
        </span>

        <div className="relative inline-block transform overflow-hidden rounded-lg bg-white text-left align-bottom shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg sm:align-middle">
          <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-medium text-gray-900">Test Rule Pattern</h3>
              <button
                onClick={handleClose}
                className="text-gray-400 hover:text-gray-600"
              >
                <X className="h-6 w-6" />
              </button>
            </div>

            <div className="space-y-4">
              {/* Pattern Input */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Regular Expression Pattern
                </label>
                <div className="relative">
                  <input
                    type="text"
                    value={pattern}
                    onChange={(e) => handlePatternChange(e.target.value)}
                    placeholder="e.g., ^starbucks|coffee|espresso$"
                    className={`w-full px-3 py-2 border rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm ${
                      !isValidPattern ? 'border-red-300' : 'border-gray-300'
                    }`}
                  />
                  {pattern && (
                    <div className="absolute right-3 top-1/2 transform -translate-y-1/2">
                      {isValidPattern ? (
                        <CheckCircle className="h-4 w-4 text-green-500" />
                      ) : (
                        <XCircle className="h-4 w-4 text-red-500" />
                      )}
                    </div>
                  )}
                </div>
                {patternError && (
                  <p className="mt-1 text-sm text-red-600">{patternError}</p>
                )}
                <p className="mt-1 text-xs text-gray-500">
                  Use regular expressions to match transaction descriptions or merchants
                </p>
              </div>

              {/* Test Text Input */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Test Text
                </label>
                <input
                  type="text"
                  value={testText}
                  onChange={(e) => setTestText(e.target.value)}
                  placeholder="e.g., Starbucks Coffee Purchase"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
                <p className="mt-1 text-xs text-gray-500">
                  Enter sample text to test against the pattern
                </p>
              </div>

              {/* Test Button */}
              <div className="flex justify-end">
                <button
                  onClick={handleTest}
                  disabled={!pattern.trim() || !testText.trim() || !isValidPattern || testRule.isPending}
                  className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {testRule.isPending ? (
                    <>
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                      Testing...
                    </>
                  ) : (
                    <>
                      <Play className="h-4 w-4 mr-2" />
                      Test Pattern
                    </>
                  )}
                </button>
              </div>

              {/* Test Results */}
              {testResult !== null && (
                <div className="mt-4 p-4 rounded-md border">
                  <div className="flex items-center">
                    {testResult.matches ? (
                      <CheckCircle className="h-5 w-5 text-green-500 mr-2" />
                    ) : (
                      <XCircle className="h-5 w-5 text-red-500 mr-2" />
                    )}
                    <span className={`font-medium ${
                      testResult.matches ? 'text-green-800' : 'text-red-800'
                    }`}>
                      {testResult.matches ? 'Pattern Matches!' : 'Pattern Does Not Match'}
                    </span>
                  </div>
                  
                  {testResult.matches && testResult.matchedText && (
                    <div className="mt-2">
                      <p className="text-sm text-gray-600">Matched text:</p>
                      <p className="text-sm font-mono bg-gray-100 p-2 rounded mt-1">
                        "{testResult.matchedText}"
                      </p>
                    </div>
                  )}

                  {!testResult.matches && (
                    <div className="mt-2">
                      <p className="text-sm text-gray-600">
                        The pattern did not match the test text. Try adjusting your regular expression.
                      </p>
                    </div>
                  )}
                </div>
              )}

              {/* Pattern Examples */}
              <div className="mt-4 p-3 bg-gray-50 rounded-md">
                <h4 className="text-sm font-medium text-gray-700 mb-2">Pattern Examples:</h4>
                <div className="space-y-1 text-xs text-gray-600">
                  <div><code className="bg-white px-1 py-0.5 rounded">^starbucks|coffee</code> - Matches text starting with "starbucks" or "coffee"</div>
                  <div><code className="bg-white px-1 py-0.5 rounded">amazon|prime</code> - Matches text containing "amazon" or "prime"</div>
                  <div><code className="bg-white px-1 py-0.5 rounded">^gas|fuel|petrol</code> - Matches text starting with "gas", "fuel", or "petrol"</div>
                  <div><code className="bg-white px-1 py-0.5 rounded">netflix|spotify|subscription</code> - Matches subscription services</div>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
            <button
              type="button"
              onClick={handleClose}
              className="w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
            >
              Close
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
