import { Link } from 'react-router-dom';
import { Button } from './figma-ui/button';
import { AlertCircle, Home, ArrowLeft } from 'lucide-react';

interface ErrorPageProps {
  type?: '404' | '500' | 'network';
  message?: string;
}

export default function ErrorPage({ type = '500', message }: ErrorPageProps) {
  const errorContent = {
    '404': {
      title: 'Page Not Found',
      description: 'The page you\'re looking for doesn\'t exist or has been moved.',
      icon: '🏈',
    },
    '500': {
      title: 'Something Went Wrong',
      description: message || 'We\'re having trouble loading this data. Please try again later.',
      icon: '⚠️',
    },
    'network': {
      title: 'Connection Error',
      description: 'Unable to connect to the server. Please check your internet connection.',
      icon: '📡',
    },
  };

  const content = errorContent[type];

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-red-50 flex items-center justify-center p-6">
      <div className="max-w-md w-full text-center space-y-8">
        {/* Error Icon */}
        <div className="flex justify-center">
          <div className="bg-white rounded-full p-6 shadow-lg">
            <div className="text-6xl">{content.icon}</div>
          </div>
        </div>

        {/* Error Content */}
        <div className="space-y-3">
          <h1 className="text-3xl text-gray-900">
            {content.title}
          </h1>
          <p className="text-lg text-gray-600">
            {content.description}
          </p>
        </div>

        {/* Error Code */}
        {type !== 'network' && (
          <div className="inline-flex items-center gap-2 px-4 py-2 bg-gray-100 rounded-lg">
            <AlertCircle className="h-4 w-4 text-gray-500" />
            <span className="text-sm text-gray-600">
              Error Code: {type.toUpperCase()}
            </span>
          </div>
        )}

        {/* Action Buttons */}
        <div className="flex flex-col sm:flex-row gap-3 justify-center pt-4">
          <Button
            asChild
            variant="outline"
            size="lg"
          >
            <Link to="/" className="flex items-center gap-2">
              <ArrowLeft className="h-4 w-4" />
              Go Back
            </Link>
          </Button>
          
          <Button
            asChild
            size="lg"
            className="bg-blue-600 hover:bg-blue-700"
          >
            <Link to="/dashboard" className="flex items-center gap-2">
              <Home className="h-4 w-4" />
              Back to Dashboard
            </Link>
          </Button>
        </div>

        {/* Help Text */}
        <div className="pt-8 border-t border-gray-200">
          <p className="text-sm text-gray-500">
            If this problem persists, please contact support or try refreshing the page.
          </p>
        </div>
      </div>
    </div>
  );
}
